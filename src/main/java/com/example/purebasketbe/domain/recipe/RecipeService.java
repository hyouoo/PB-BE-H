package com.example.purebasketbe.domain.recipe;

import com.example.purebasketbe.domain.product.ProductRepository;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.recipe.dto.RecipeRequestDto;
import com.example.purebasketbe.domain.recipe.dto.RecipeResponseDto;
import com.example.purebasketbe.domain.recipe.entity.Recipe;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import com.example.purebasketbe.global.s3.S3Handler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final ProductRepository productRepository;
    private final S3Handler s3Handler;

    private final int RECIPES_PER_PAGE = 10;

    @Transactional(readOnly = true)
    public Page<RecipeResponseDto> getRecipes(int page) {

        Pageable pageable = PageRequest.of(page, RECIPES_PER_PAGE);
        Page<Recipe> recipes = recipeRepository.findAll(pageable);

        return recipes.map(RecipeResponseDto::from);
    }

    @Transactional(readOnly = true)
    public RecipeResponseDto getRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
                () -> new CustomException(ErrorCode.RECIPE_NOT_FOUND));
        List<Product> productList = recipe.getProductList();

        return RecipeResponseDto.of(recipe, productList);
    }

    @Transactional
    public void registerRecipe(RecipeRequestDto requestDto, MultipartFile file) {
        checkExistRecipeByName(requestDto);
        String imgUrl = s3Handler.makeUrl(file);

        Recipe recipe = Recipe.from(requestDto, imgUrl);
        for (Long productId : requestDto.productIdList()) {
            Product product = findValidProduct(productId);
            recipe.addProduct(product);
        }

        recipeRepository.save(recipe);
        s3Handler.uploadImages(imgUrl, file);
    }

    @Transactional
    public void deleteRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() ->
                new CustomException(ErrorCode.RECIPE_NOT_FOUND)
        );
        s3Handler.deleteImage(recipe.getImgUrl());
        recipeRepository.delete(recipe);
    }

    private Product findValidProduct(Long productId) {
        return productRepository.findByIdAndDeleted(productId, false).orElseThrow(() ->
                new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
        );
    }

    private void checkExistRecipeByName(RecipeRequestDto requestDto) {
        if (recipeRepository.existsByName(requestDto.name())) {
            throw new CustomException(ErrorCode.RECIPE_ALREADY_EXISTS);
        }
    }
}
