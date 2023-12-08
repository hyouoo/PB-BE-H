package com.example.purebasketbe.domain.cart;

import com.example.purebasketbe.domain.cart.dto.CartRequestDto;
import com.example.purebasketbe.domain.cart.dto.CartResponseDto;
import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.global.tool.LoginAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    @PostMapping("/{productId}")
    public ResponseEntity<Void> addToCart(@PathVariable Long productId, @RequestBody CartRequestDto requestDto,
                                          @LoginAccount Member member) {
        cartService.addToCart(productId, requestDto, member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping
    public ResponseEntity<List<CartResponseDto>> getCartList(@LoginAccount Member member) {
        List<CartResponseDto> responseBody = cartService.getCartList(member);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Void> updateCart(@PathVariable Long productId, @RequestBody CartRequestDto requestDto,
                                           @LoginAccount Member member) {
        cartService.updateCart(productId, requestDto, member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long productId, @LoginAccount Member member) {
        cartService.deleteCart(productId, member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/recipes/{recipeId}")
    public ResponseEntity<Void> addRecipeRelatedProductsToCart(@PathVariable Long recipeId,
                                                               @LoginAccount Member member) {
        cartService.addRecipeRelatedProductsToCarts(recipeId, member);
        return ResponseEntity.status(HttpStatus.CREATED).location(URI.create("/api/carts")).build();
    }
}
