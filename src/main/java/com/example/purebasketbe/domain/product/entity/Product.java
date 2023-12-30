package com.example.purebasketbe.domain.product.entity;

import com.example.purebasketbe.domain.product.dto.ProductRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Getter
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int price;

    private String info;

    private String category;

    @Enumerated(EnumType.STRING)
    private Event event;

    private int discountRate;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Column(nullable = false)
    private boolean deleted;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Stock stock;

    @BatchSize(size = 21)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @Builder
    private Product(String name, Integer price,  String info,
                    String category, Event event, Integer discountRate, Stock stock) {
        this.name = name;
        this.price = price;
        this.info = info;
        this.category = category;
        this.event = event;
        this.discountRate = discountRate;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
        this.deleted = false;
        this.stock = stock;
    }

    public static Product from(ProductRequestDto requestDto) {
        return Product.builder()
                .name(requestDto.name())
                .price(requestDto.price())
                .info(requestDto.info())
                .category(requestDto.category())
                .event(requestDto.event())
                .discountRate(requestDto.discountRate())
                .build();
    }

    public void update(ProductRequestDto requestDto) {
        this.name = requestDto.name() == null ? this.name : requestDto.name();
        this.price = requestDto.price() == null ? this.price : requestDto.price();
        this.info = requestDto.info() == null ? this.info : requestDto.info();
        this.category = requestDto.category() == null ? this.category : requestDto.category();
        this.event = requestDto.event() == null ? this.event : requestDto.event();
        this.discountRate = requestDto.discountRate() == null ? this.discountRate : requestDto.discountRate();
        this.modifiedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.name += "-deleted-" + UUID.randomUUID();
        this.modifiedAt = LocalDateTime.now();
        this.deleted = true;
    }

}