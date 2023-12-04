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

    private Event event;

    private Integer discountRate;

    private Integer salesCount;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Column(nullable = false)
    private boolean deleted;

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
                .name(requestDto.getName())
                .price(requestDto.getPrice())
                .stock(requestDto.getStock())
                .info(requestDto.getInfo())
                .category(requestDto.getCategory())
                .event(requestDto.getEvent())
                .discountRate(requestDto.getDiscountRate())
                .build();
    }

    public void update(ProductRequestDto requestDto) {
        this.name = requestDto.getName() == null ? this.name : requestDto.getName();
        this.price = requestDto.getPrice() == null ? this.price : requestDto.getPrice();
        this.stock = requestDto.getStock() == null ? this.stock : this.stock + requestDto.getStock();
        this.info = requestDto.getInfo() == null ? this.info : requestDto.getInfo();
        this.category = requestDto.getCategory() == null ? this.category : requestDto.getCategory();
        this.event = requestDto.getEvent() == null ? this.event : requestDto.getEvent();
        this.discountRate = requestDto.getDiscountRate() == null ? this.discountRate : requestDto.getDiscountRate();
    }


    public void incrementSalesCount(int amount) {
        this.salesCount += amount;
    }

    public void decrementStock(int amount) {
        this.stock -= amount;
    }
}