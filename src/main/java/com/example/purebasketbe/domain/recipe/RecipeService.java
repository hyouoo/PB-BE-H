package com.example.purebasketbe.domain.recipe;

import com.example.purebasketbe.domain.product.ProductRepository;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.recipe.dto.RecipeRequestDto;
import com.example.purebasketbe.domain.recipe.dto.RecipeResponseDto;
import com.example.purebasketbe.domain.recipe.entity.Recipe;
import com.example.purebasketbe.domain.recipe.entity.RecipeProduct;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final ProductRepository productRepository;
    private final S3Template s3Template;

    @Value("${aws.bucket.name}")
    private String bucket;
    @Value("${spring.cloud.aws.region.static}")
    private String region;
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
        List<Product> productList = recipe.getRecipeProductList().stream()
                .map(RecipeProduct::getProduct).toList();

        return RecipeResponseDto.of(recipe, productList);
    }

    @Transactional
    public void registerRecipe(RecipeRequestDto requestDto, MultipartFile file) {
        checkExistRecipeByName(requestDto);
        String key = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String imgUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);

        Recipe recipe = Recipe.from(requestDto, imgUrl);
        for (Long productId : requestDto.getProductIdList()) {
            Product product = findValidProduct(productId);
            RecipeProduct recipeProduct = RecipeProduct.of(recipe, product);
            recipe.addRecipeProduct(recipeProduct);
        }

        recipeRepository.save(recipe);
        // S3 저장은 commit이 된 이후에 수행되어야 한다.
        uploadImage(file, key);
    }

    @Transactional
    public void deleteProduct(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() ->
                new CustomException(ErrorCode.RECIPE_NOT_FOUND)
        );
        recipeRepository.delete(recipe);
        // ToDo: S3에서 사진 삭제하기 코드 추가
    }

    // ToDo: productService에 있는 메서드와 합친 후에 S3 관련 폴더 생성하기
    private void uploadImage(MultipartFile file, String key) {
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INVALID_IMAGE);
        }
        ObjectMetadata metadata = ObjectMetadata.builder().contentType("text/plain").build();

        s3Template.upload(bucket, key, inputStream, metadata);
    }

    private Product findValidProduct(Long productId) {
        return productRepository.findByIdAndDeleted(productId, false).orElseThrow(() ->
                new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
        );
    }

    private void checkExistRecipeByName(RecipeRequestDto requestDto) {
        if (recipeRepository.existsByName(requestDto.getName())) {
            throw new CustomException(ErrorCode.RECIPE_ALREADY_EXISTS);
        }
    }
}
