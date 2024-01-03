package com.example.purebasketbe.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@RequiredArgsConstructor
@Configuration
@EnableRedisRepositories
public class RedisConfig {
    @Value("${spring.data.redis.sentinel.master}")
    private String sentinel1;
    @Value("${spring.data.redis.sentinel.replica1}")
    private String sentinel2;
    @Value("${spring.data.redis.sentinel.replica2}")
    private String sentinel3;
    @Value("${spring.data.redis.sentinel.port}")
    private int port;

    // local에서만 필요한 포트
    @Value("${spring.data.redis.sentinel.port2}")
    private int port2;
    @Value("${spring.data.redis.sentinel.port3}")
    private int port3;

    @Value("${spring.data.redis.password}")
    private String password;


    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisSentinelConfiguration sentinelConfig  = new RedisSentinelConfiguration()
                .master("mymaster")
                .sentinel(sentinel1, port)
                .sentinel(sentinel2, port2)
                .sentinel(sentinel3, port3);

        sentinelConfig.setPassword(password);
        return new LettuceConnectionFactory(sentinelConfig );

    }

    // serializer 설정으로 redis-cli를 통해 직접 데이터를 조회할 수 있도록 설정
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        return redisTemplate;
    }

    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheManager redisCacheManager = RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration()).build();
        return redisCacheManager;
    }

    private RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
                );
    }

//    @Bean
//    @Primary
//    public LettuceConnectionFactory redisConnectionFactory() {
//        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
//    }

//    @Bean(name = "redisCacheConnectionFactory")
//    public RedisConnectionFactory redisCacheConnectionFactory() {
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
//        redisStandaloneConfiguration.setHostName(cacheHost);
//        redisStandaloneConfiguration.setPort(cachePort);
//        return new LettuceConnectionFactory(redisStandaloneConfiguration);
//    }

//    @Bean(name = "redisCacheTemplate")
//    public RedisTemplate<String, Object> redisTemplate(@Qualifier("redisCacheConnectionFactory") RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(connectionFactory);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//
//        return redisTemplate;
//    }

}