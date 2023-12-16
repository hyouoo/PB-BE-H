package com.example.purebasketbe.domain.cart;

import com.example.purebasketbe.domain.cart.dto.CartRequestDto;
import com.example.purebasketbe.domain.cart.dto.CartResponseDto;
import com.example.purebasketbe.domain.cart.entity.Cart;
import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.product.ImageRepository;
import com.example.purebasketbe.domain.product.ProductRepository;
import com.example.purebasketbe.domain.product.entity.Image;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.recipe.RecipeRepository;
import com.example.purebasketbe.domain.recipe.entity.Recipe;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock
    CartRepository cartRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    RecipeRepository recipeRepository;

    @Mock
    ImageRepository imageRepository;

    @InjectMocks
    CartService cartService;


    private Long productId;
    private Member member;
    private CartRequestDto requestDto;
    private Product product;

    @BeforeEach
    void setUp() {
        productId = 1L;
        member = Member.builder().build();
        requestDto = new CartRequestDto(1);
        product = Product.builder().price(10000).stock(10).discountRate(0).build();
    }

    @Nested
    @DisplayName("장바구니 담기")
    class AddToCart {

        @Test
        @DisplayName("장바구니 담기 성공")
        void addToCartSuccess() {
            // given
            given(productRepository.findByIdAndDeleted(productId, false)).willReturn(Optional.of(product));
            given(cartRepository.existsProduct(product)).willReturn(false);

            // when
            cartService.addToCart(productId, requestDto, member);

            // then
            verify(cartRepository).save(any(Cart.class));
        }

        @Test
        @DisplayName("장바구니 담기 실패 - 존재하지 않는 상품")
        void addToCartFail1() {
            // given
            given(productRepository.findByIdAndDeleted(productId, false)).willReturn(Optional.empty());

            // when
            Exception exception = assertThrows(CustomException.class,
                    () -> cartService.addToCart(productId, requestDto, member)
            );

            // then
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("장바구니 담기 실패 - 이미 등록된 상품")
        void addToCartFail2() {
            // given

            given(productRepository.findByIdAndDeleted(productId, false)).willReturn(Optional.of(product));
            given(cartRepository.existsProduct(product)).willReturn(true);

            // when
            Exception exception = assertThrows(CustomException.class,
                    () -> cartService.addToCart(productId, requestDto, member)
            );

            // then
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.PRODUCT_ALREADY_ADDED.getMessage());

        }
    }

    @Nested
    @DisplayName("장바구니 리스트 조회")
    class GetCartList {
        @Test
        @DisplayName("장바구니 리스트 조회 성공")
        void getCartListSuccess() {
            // given
            List<Image> imageList = List.of(mock(Image.class), mock(Image.class));
            List<Cart> cartList = List.of(Cart.builder().product(product).build());
            given(cartRepository.findAllByMember(member)).willReturn(cartList);
            given(productRepository.findByIdAndDeleted(any(), eq(false))).willReturn(Optional.of(product));
            given(imageRepository.findAllByProductId(any())).willReturn(imageList);

            // when
            List<CartResponseDto> result = cartService.getCartList(member);

            // then
            assertThat(result.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("장바구니 리스트 조회 실패 - 존재하지 않은 상품")
        void getCartListFail1() {
            // given
            List<Cart> cartList = List.of(Cart.builder().product(product).build());
            given(cartRepository.findAllByMember(member)).willReturn(cartList);
            given(productRepository.findByIdAndDeleted(any(), eq(false))).willReturn(Optional.empty());

            // when
            Exception exception = assertThrows(CustomException.class,
                    () -> cartService.getCartList(member)
            );

            // then
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("장바구니 리스트 조회 실패 - 유효하지 않은 이미지")
        void getCartListFail2() {
            // given
            List<Cart> cartList = List.of(Cart.builder().product(product).build());
            given(cartRepository.findAllByMember(member)).willReturn(cartList);
            given(productRepository.findByIdAndDeleted(any(), eq(false))).willReturn(Optional.of(product));
            given(imageRepository.findAllByProductId(any())).willReturn(new ArrayList<>());

            // when
            Exception exception = assertThrows(CustomException.class,
                    () -> cartService.getCartList(member)
            );

            // then
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.INVALID_IMAGE.getMessage());
        }
    }

    @Nested
    @DisplayName("장바구니 업데이트")
    class UpdateCart {

        @Test
        @DisplayName("장바구니 업데이트 성공")
        void updateCartSuccess() {
            // given
            Cart cart = mock(Cart.class);
            given(cartRepository.findByProductIdAndMember(productId, member)).willReturn(Optional.of(cart));

            // when
            cartService.updateCart(productId, requestDto, member);

            // then
            verify(cart).changeAmount(requestDto);
        }

        @Test
        @DisplayName("장바구니 업데이트 실패")
        void updateCartFail() {
            // given
            given(cartRepository.findByProductIdAndMember(productId, member)).willReturn(Optional.empty());

            // when
            Exception exception = assertThrows(CustomException.class,
                    () -> cartService.updateCart(productId, requestDto, member)
            );

            // then
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.INVALID_CART_ITEM.getMessage());
        }
    }

    @Nested
    @DisplayName("장바구니 삭제")
    class DeleteCart {

        @Test
        @DisplayName("장바구니 삭제 성공")
        void deleteCartSuccess() {
            // given
            Cart cart = mock(Cart.class);
            given(cartRepository.findByProductIdAndMember(productId, member)).willReturn(Optional.of(cart));

            // when
            cartService.deleteCart(productId, member);

            // then
            verify(cartRepository).delete(cart);
        }

        @Test
        @DisplayName("장바구니 삭제 실패")
        void deleteCartFail() {
            // given
            given(cartRepository.findByProductIdAndMember(productId, member)).willReturn(Optional.empty());

            // when
            Exception exception = assertThrows(CustomException.class,
                    () -> cartService.deleteCart(productId, member)
            );

            // then
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.INVALID_CART_ITEM.getMessage());
        }
    }

    @Nested
    @DisplayName("레시피 관련 상품 장바구니 추가")
    class AddRecipeRelatedProductsToCart {
        @Test
        @DisplayName("레시피 관련 상품 장바구니 추가 성공")
        void addRecipeRelatedProductsToCartSuccess() {
            // given
            Long recipeId = 1L;
            List<Product> productList = List.of(product);
            Recipe recipe = Recipe.builder()
                    .productList(productList)
                    .build();

            given(recipeRepository.findById(recipeId)).willReturn(Optional.of(recipe));

            // when
            cartService.addRecipeRelatedProductsToCarts(recipeId, member);

            // then
            verify(cartRepository).deleteAllByMemberAndProductIn(member, productList);
            verify(cartRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("레시피 관련 상품 장바구니 추가 실패")
        void addRecipeRelatedProductsToCartFail() {
            // given
            Long recipeId = 1L;
            given(recipeRepository.findById(recipeId)).willReturn(Optional.empty());

            // when
            Exception exception = assertThrows(CustomException.class,
                    () -> cartService.addRecipeRelatedProductsToCarts(recipeId, member)
            );

            // then
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.RECIPE_NOT_FOUND.getMessage());
        }
    }

}