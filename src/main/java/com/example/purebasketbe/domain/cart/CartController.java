package com.example.purebasketbe.domain.cart;

import com.example.purebasketbe.domain.cart.dto.CartRequestDto;
import com.example.purebasketbe.domain.cart.dto.CartResponseDto;
import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.global.tool.LoginAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<String> addToCart(@PathVariable Long productId, @RequestBody CartRequestDto requestDto,
                                            @LoginAccount Member member)
   {
       try
       {
           cartService.addToCart(productId, requestDto,member);
       }
       catch (Exception e)
       {
           return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
       }
       return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CartResponseDto>> getCart(@LoginAccount Member member) {

        List<CartResponseDto> responseBody = cartService.getCart(member);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<String> changeCart(@PathVariable Long productId, @RequestBody CartRequestDto requestDto,
                                             @LoginAccount Member member, Pageable pageable)
    {
        try
        {
            cartService.changeCart(productId, requestDto, member);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteCart(@PathVariable Long productId, @LoginAccount Member member,
                                             Pageable pageable)
    {
        try
        {
            cartService.deleteCart(productId);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>("해당하는 상품이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/recipes/{recipeId}")
    public ResponseEntity<Void> addRecipeRelatedProductsToCart(@PathVariable Long recipeId,
                                                               @LoginAccount Member member) {
        cartService.addRecipeRelatedProductsToCarts(recipeId, member);
        return ResponseEntity.status(HttpStatus.CREATED).location(URI.create("/api/carts")).build();
    }
}
