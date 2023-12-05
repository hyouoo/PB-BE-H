package com.example.purebasketbe.domain.recipe;

import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.recipe.dto.RecipeDetailResponseDto;
import com.example.purebasketbe.domain.recipe.dto.RecipeResponseDto;
import com.example.purebasketbe.domain.recipe.entity.Recipe;
import com.example.purebasketbe.domain.recipe.entity.RecipeProduct;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;

    private final int RECIPES_PER_PAGE = 10;

    @Transactional(readOnly = true)

    public Page<RecipeResponseDto> getRecipes(int page) {

        Pageable pageable = PageRequest.of(page, RECIPES_PER_PAGE);
        Page<Recipe> recipes = recipeRepository.findAll(pageable);

        return recipes.map(RecipeResponseDto::from);
    }

    @Transactional(readOnly = true)
    public RecipeDetailResponseDto getRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
                () -> new CustomException(ErrorCode.RECIPE_NOT_FOUND));
        List<Product> productList = recipe.getRecipeProductList().stream()
                .map(RecipeProduct::getProduct).toList();

        return RecipeDetailResponseDto.of(recipe, productList);

    }
}
