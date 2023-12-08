package com.example.purebasketbe.domain.product.entity;

import com.example.purebasketbe.domain.product.dto.ProductRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    private String info;

    private String category;

    @Enumerated(EnumType.STRING)
    private Event event;

    private Integer discountRate;

    private Integer salesCount;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Column(nullable = false)
    private boolean deleted;

    @OneToMany(mappedBy = "product")
    private List<Image> images = new ArrayList<>();;


    @Builder
    private Product(String name, Integer price, Integer stock, String info,
                    String category, Event event, Integer discountRate) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.info = info;
        this.category = category;
        this.event = event;
        this.discountRate = discountRate;
        this.salesCount = 0;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
        this.deleted = false;
    }

    public static Product from(ProductRequestDto requestDto) {
        return Product.builder()
                .name(requestDto.name())
                .price(requestDto.price())
                .stock(requestDto.stock())
                .info(requestDto.info())
                .category(requestDto.category())
                .event(requestDto.event())
                .discountRate(requestDto.discountRate())
                .build();
    }

    public void update(ProductRequestDto requestDto) {
        this.name = requestDto.name() == null ? this.name : requestDto.name();
        this.price = requestDto.price() == null ? this.price : requestDto.price();
        this.stock = requestDto.stock() == null ? this.stock : this.stock + requestDto.stock();
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

    public void incrementSalesCount(int amount) {
        this.salesCount += amount;
    }

    public void decrementStock(int amount) {
        this.stock -= amount;
    }
}