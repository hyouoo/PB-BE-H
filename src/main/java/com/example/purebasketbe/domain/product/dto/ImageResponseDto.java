package com.example.purebasketbe.domain.product.dto;

import com.example.purebasketbe.domain.product.entity.Product;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageResponseDto {

    private Long productId;

    private List<String> imgUrlList;

    @Builder
    private ImageResponseDto(Long productId, List<String> imgUrlList) {
        this.productId = productId;
        this.imgUrlList = imgUrlList;
    }

    public static ImageResponseDto of(Product product, List<String> imgUrlList) {
        return ImageResponseDto.builder()
                .productId(product.getId())
                .imgUrlList(imgUrlList)
                .build();
    }
}
