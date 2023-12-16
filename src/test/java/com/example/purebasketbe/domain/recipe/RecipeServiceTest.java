package com.example.purebasketbe.domain.recipe;

import com.example.purebasketbe.domain.product.ProductRepository;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.recipe.dto.RecipeRequestDto;
import com.example.purebasketbe.domain.recipe.dto.RecipeResponseDto;
import com.example.purebasketbe.domain.recipe.entity.Recipe;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import com.example.purebasketbe.global.s3.S3Handler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {
    @Mock
    RecipeRepository recipeRepository;

    @Mock
    ProductRepository productRepository;
    @Mock
    S3Handler s3Handler;

    @InjectMocks
    RecipeService recipeService;

    @Test
    @DisplayName("레시피 목록 조회")
    void getRecipes() {
        // given
        int page = 0;
        final int RECIPES_PER_PAGE = 10;
        Pageable pageRequest = PageRequest.of(page, RECIPES_PER_PAGE);
        List<Recipe> recipeList = new ArrayList<>();
        Recipe recipe = Recipe.builder().build();
        recipeList.add(recipe);
        Page<Recipe> recipes = new PageImpl<>(recipeList, pageRequest,
                recipeList.size());
//        Page<Recipe> recipes = mock(Page.class);

        given(recipeRepository.findAll(pageRequest)).willReturn(recipes);
//        given(recipeRepository.findAll(any(Pageable.class)).willReturn(recipes);

        // when
        Page<RecipeResponseDto> result = recipeService.getRecipes(page);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        verify(recipeRepository).findAll(any(Pageable.class));
    }

    @Nested
    @DisplayName("레시피 상세 조회")
    class getRecipe {
        @Test
        @DisplayName("레시피 상세 조회 성공")
        void getRecipeSuccess() {
            // given
            Long recipeId = 1L;
            List<Product> productList = new ArrayList<>();
            Recipe recipe = Recipe.builder().name("test recipe").productList(productList).build();
            given(recipeRepository.findById(any())).willReturn(Optional.of(recipe));

            // when
            RecipeResponseDto result = recipeService.getRecipe(recipeId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("test recipe");
            verify(recipeRepository).findById(recipeId);
        }

        @Test
        @DisplayName("레시피 상세 조회 실패")
        void getRecipeFail() {
            // given
            Long recipeId = 1L;
            given(recipeRepository.findById(any())).willReturn(Optional.empty());

            // when
            Exception exception = assertThrows(CustomException.class,
                    () -> recipeService.getRecipe(recipeId)
            );

            // then
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.RECIPE_NOT_FOUND.getMessage());
        }


    }

    @Nested
    @DisplayName("레시피 등록")
    class RegisterRecipe {

        private MultipartFile file;

        private RecipeRequestDto requestDto;

        @BeforeEach
        void setUp() {
            file = mock(MultipartFile.class);
            List<Long> productIdList = new ArrayList<>(List.of(1L, 2L));
            requestDto = new RecipeRequestDto("test recipe", "info", productIdList);
        }

        @Test
        @DisplayName("레시피 등록 성공")
        void registerRecipeSuccess() {
            // given
            Product product = Product.builder().price(1000).stock(10).discountRate(0).build();
            given(recipeRepository.existsByName(requestDto.name())).willReturn(false);
            given(s3Handler.makeUrl(file)).willReturn("mockUrl");
            given(productRepository.findByIdAndDeleted(any(), eq(false))).willReturn(Optional.of(product));

            // when
            recipeService.registerRecipe(requestDto, file);

            // then
            verify(s3Handler, times(1)).makeUrl(file);
            verify(recipeRepository, times(1)).existsByName(requestDto.name());
            verify(recipeRepository, times(1)).save(any(Recipe.class));
            verify(s3Handler, times(1)).uploadImages("mockUrl", file);
        }

        @Test
        @DisplayName("레시피 등록 실패 - 이미 존재하는 레시피")
        void registerRecipeFail1() {
            // given
            given(recipeRepository.existsByName(requestDto.name())).willReturn(true);

            // when
            Exception exception = assertThrows(CustomException.class,
                    () -> recipeService.registerRecipe(requestDto, file)
            );

            // then
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.RECIPE_ALREADY_EXISTS.getMessage());
        }

        @Test
        @DisplayName("레시피 등록 실패 - 존재하지 않는 관련 상품")
        void registerRecipeFail2() {
            // given
            given(recipeRepository.existsByName(requestDto.name())).willReturn(false);
            given(productRepository.findByIdAndDeleted(any(), eq(false))).willReturn(Optional.empty());

            // when
            Exception exception = assertThrows(CustomException.class,
                    () -> recipeService.registerRecipe(requestDto, file)
            );

            // then
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("레시피 삭제")
    class DeleteRecipe {
        @Test
        @DisplayName("레시피 삭제 성공")
        void deleteRecipeSuccess() {
            // given
            Long recipeId = 1L;
            Recipe recipe = Recipe.builder().build();
            given(recipeRepository.findById(any())).willReturn(Optional.of(recipe));

            // when
            recipeService.deleteRecipe(recipeId);

            // then
            verify(s3Handler, times(1)).deleteImage(any());
            verify(recipeRepository, times(1)).delete(recipe);
        }

        @Test
        @DisplayName("레시피 삭제 실패")
        void deleteRecipeFail() {
            // given
            Long recipeId = 1L;
            given(recipeRepository.findById(any())).willReturn(Optional.empty());

            // when
            Exception exception = assertThrows(CustomException.class,
                    () -> recipeService.deleteRecipe(recipeId)
            );

            // then
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.RECIPE_NOT_FOUND.getMessage());
        }
    }
}