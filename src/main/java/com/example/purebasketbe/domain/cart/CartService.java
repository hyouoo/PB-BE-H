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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final RecipeRepository recipeRepository;
    private final ImageRepository imageRepository;

    @Transactional
    public void addToCart(Long productId, CartRequestDto requestDto, Member member) {
        Product product = findAndValidateProduct(productId);
        chcekDuplicate(product);
        Cart newCart = Cart.of(product, member, requestDto);
        cartRepository.save(newCart);
    }

    @Transactional(readOnly = true)
    public List<CartResponseDto> getCartList(Member member) {
        return cartRepository.findAllByMember(member).stream()
                .map(cart -> {
                    Product product = findAndValidateProduct(cart.getProduct().getId());
                    Image image = findImage(product.getId());
                    return CartResponseDto.of(product, image, cart);
                }).toList();
    }

    @Transactional
    public void updateCart(Long productId, CartRequestDto requestDto, Member member) {
        Cart cart = findAndValidateCart(productId, member);
        cart.changeAmount(requestDto);
    }

    @Transactional
    public void deleteCart(Long productId, Member member) {
        Cart cart = findAndValidateCart(productId, member);
        cartRepository.delete(cart);
    }

    @Transactional
    public void addRecipeRelatedProductsToCarts(Long recipeId, Member member) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() ->
                new CustomException(ErrorCode.RECIPE_NOT_FOUND)
        );

        List<Product> productList = recipe.getProductList();
        cartRepository.deleteAllByMemberAndProductIn(member, productList);
        List<Cart> cartList = productList.stream().map(product -> Cart.of(product, member, null)).toList();
        cartRepository.saveAll(cartList);
    }

    private Product findAndValidateProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
        );
        if (product.getStock() <= 0 || product.isDeleted()) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_PRODUCT);
        }
        return product;
    }

    private void chcekDuplicate(Product product) {
        if (cartRepository.existsProduct(product)) {
            throw new CustomException(ErrorCode.PRODUCT_ALREADY_ADDED);
        }
    }

    private Cart findAndValidateCart(Long productId, Member member) {
        return cartRepository.findByProductIdAndMember(productId, member).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_CART_ITEM)
        );
    }

    private Image findImage(Long productId) {
        return imageRepository.findAllByProductId(productId).stream().findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_IMAGE));
    }
}