package com.example.purebasketbe.domain.cart;

import com.example.purebasketbe.domain.cart.dto.CartRequestDto;
import com.example.purebasketbe.domain.cart.dto.CartResponseDto;
import com.example.purebasketbe.domain.cart.entity.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;

//    @PostMapping("/{productId}")
//    public ResponseEntity<Object> addToCart(@PathVariable Long productId, @RequestBody CartRequestDto requestDto)
//    {
//        cartService.addToCart(productId, requestDto);
//    }
//
//    @GetMapping("")
//    public CartResponseDto getCart(@AuthenticationPrincipal UserDetai)
//    {
//        cartService.getCart();
//    }
//
//    @PutMapping("/{productId}")
//    public changeCart
//
//    @DeleteMapping("/{productId}")
//    public deleteCart
}
