package com.example.purebasketbe.domain.cart;

import com.example.purebasketbe.domain.cart.dto.CartRequestDto;
import com.example.purebasketbe.domain.cart.dto.CartResponseDto;
import com.example.purebasketbe.domain.cart.entity.Cart;
import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.product.ProductRepository;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.recipe.RecipeRepository;
import com.example.purebasketbe.domain.recipe.entity.Recipe;
import com.example.purebasketbe.domain.recipe.entity.RecipeProduct;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final RecipeRepository recipeRepository;

    @Transactional
    public void addToCart(Long productId, CartRequestDto requestDto, Member member) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NullPointerException("해당 상품이 존재하지 않습니다.")
        );

        Cart cart = cartRepository.save(Cart.of(requestDto, member, product));
    }

    @Transactional(readOnly = true)
    public List<CartResponseDto> getCart(Member member) {

        List<Cart> cartList = cartRepository.findAllByMember(member);

        return cartList.stream().map(cart -> {
            Product product = getProductById(cart.getProduct().getId());
            return CartResponseDto.of(product, cart);
        }).toList();
    }

    @Transactional
    public void changeCart(Long productId, CartRequestDto requestDto, Member member) {
        Cart cart = cartRepository.findByProductId(productId).orElseThrow(
                () -> new NullPointerException("해당하는 제품이 존재하지 않습니다.")
        );

        cart.updateCart(requestDto);
    }

    @Transactional
    public void deleteCart(Long productId) {
        Cart cart = cartRepository.findByProductId(productId).orElseThrow(
                () -> new NullPointerException("해당하는 제품이 존재하지 않습니다.")
        );

        cartRepository.delete(cart);
    }


    @Transactional
    public void addRecipeRelatedProductsToCarts(Long recipeId, Member member) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() ->
                new CustomException(ErrorCode.RECIPE_NOT_FOUND)
        );

        List<Product> productList = recipe.getRecipeProductList().stream().map(RecipeProduct::getProduct).toList();
        cartRepository.deleteAllByMemberAndProductIn(member, productList);
        List<Cart> cartList = productList.stream().map(product -> Cart.of(product, member)).toList();
        cartRepository.saveAll(cartList);
    }

    private Product getProductById(Long id) {
        return cartRepository.findById(id).orElseThrow(
                () -> new NullPointerException("해당하는 상품이 없습니다.")
        ).getProduct();
    }

}