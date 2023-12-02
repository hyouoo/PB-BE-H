package com.example.purebasketbe.domain.product.entity;

import com.example.purebasketbe.domain.product.dto.ImageRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {

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
    private ProductImage(String name, String imgUrl, Product product) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.product = product;
    }

    public static ProductImage from(ImageRequestDto requestDto, Product product) {
        return ProductImage.builder()
            .name(requestDto.getName())
            .imgUrl(requestDto.getImgUrl())
            .product(product)
            .build();
    }
}
