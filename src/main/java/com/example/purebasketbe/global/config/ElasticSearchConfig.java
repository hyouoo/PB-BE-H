package com.example.purebasketbe.global.config;

import com.example.purebasketbe.domain.product.ProductSearchRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.elasticsearch.support.HttpHeaders;


@Configuration
@EnableElasticsearchRepositories(basePackageClasses = ProductSearchRepository.class)
public class ElasticSearchConfig extends ElasticsearchConfiguration {

    @Value("${elasticsearch.uri}")
    private String esUrl;
    @Value("${elasticsearch.apiKey}")
    private String apiKey;
    @Value("${spring.elasticsearch.username")
    private String username;
    @Value("${spring.elasticsearch.password")
    private String password;

    @Override
    public ClientConfiguration clientConfiguration() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "ApiKey " + apiKey);
        return ClientConfiguration.builder()
                .connectedTo(esUrl)
                .withDefaultHeaders(headers)
//                .withBasicAuth(username, password)
                .build();
    }

}
