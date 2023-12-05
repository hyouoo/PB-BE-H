package com.example.purebasketbe.domain.recipe.dto;

import com.example.purebasketbe.domain.recipe.entity.Recipe;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RecipeResponseDto {
    private final Long id;
    private final String name;
    private final String info;
    private final String imgUrl;

    @Builder
    private RecipeResponseDto(Long id, String name, String info, String imgUrl) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.imgUrl = imgUrl;
    }

    public static RecipeResponseDto from(Recipe recipe) {
        return RecipeResponseDto.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .info(recipe.getInfo())
                .imgUrl(recipe.getImgUrl())
                .build();
    }
}
