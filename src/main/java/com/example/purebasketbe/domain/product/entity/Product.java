package com.example.purebasketbe.domain.product.entity;

import com.example.purebasketbe.domain.product.dto.ProductRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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

    @Column(nullable = false)
    private int quantity;

    private String info;

    private String category;

    private Event event;

    private int count;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Column(nullable = false)
    private boolean deleted;

    @Builder
    private Product(String name, int price, int quantity, String info, String category, Event event) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.info = info;
        this.category = category;
        this.event = event;
        count = 0;
        createdAt = LocalDateTime.now();
        modifiedAt = LocalDateTime.now();
        deleted = false;
    }

    public static Product from(ProductRequestDto requestDto) {
        return Product.builder()
            .name(requestDto.getName())
            .price(requestDto.getPrice())
            .quantity(requestDto.getQuantity())
            .info(requestDto.getInfo())
            .category(requestDto.getCategory())
            .event(requestDto.getEvent())
            .build();
    }


}
