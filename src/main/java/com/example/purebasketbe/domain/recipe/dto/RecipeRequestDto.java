package com.example.purebasketbe.domain.recipe.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class RecipeRequestDto {

    @NotBlank(message = "레시피명을 입력해 주세요.")
    private String name;

    private String info;

    private List<Long> productIdList;
}
