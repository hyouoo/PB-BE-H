package com.example.purebasketbe.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    private Image(String name, String imgUrl, Product product) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.product = product;
    }

    public static Image of(String name, String imgUrl, Product product) {
        return Image.builder()
                .name(name)
                .imgUrl(imgUrl)
                .product(product)
                .build();
    }
}
