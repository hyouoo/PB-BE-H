package com.example.purebasketbe.domain.product.entity;

import com.example.purebasketbe.domain.product.dto.ProductRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "stock")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int stock;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    private Stock(int stock, Product product) {
        this.stock = stock;
        this.product = product;
    }

    public static Stock of(ProductRequestDto requestDto, Product product) {
        return Stock.builder()
                .stock(requestDto.stock())
                .product(product)
                .build();
    }

    public void update(int stock) {
        this.stock = stock;
    }


}
