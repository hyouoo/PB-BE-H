package com.example.purebasketbe.domain.product.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageRequestDto {

    private String name;

    private String imgUrl;

}
