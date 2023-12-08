package com.example.purebasketbe.domain.product.dto;

import com.example.purebasketbe.domain.product.entity.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDetailResponseDto extends ProductResponseDto {

    private List<String> imgUrlList;

    private ProductDetailResponseDto(Product product, List<String> imgUrlList) {
        super(product.getId(), product.getName(), product.getPrice(), product.getStock(),
                product.getInfo(), product.getCategory(), product.getEvent(), product.getDiscountRate());
        this.imgUrlList = imgUrlList;
    }

    public static ProductDetailResponseDto of(Product product, List<String> imgUrlList) {
        return new ProductDetailResponseDto(product, imgUrlList);
    }
}
