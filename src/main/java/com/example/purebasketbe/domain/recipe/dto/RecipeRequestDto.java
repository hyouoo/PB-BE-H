package com.example.purebasketbe.domain.recipe.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;


public record RecipeRequestDto(

        @NotBlank(message = "레시피명을 입력해 주세요.")
        String name,

        String info,

        List<Long> productIdList
) {
}
