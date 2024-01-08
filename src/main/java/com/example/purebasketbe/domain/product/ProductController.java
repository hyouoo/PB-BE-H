package com.example.purebasketbe.domain.product;

import com.example.purebasketbe.domain.product.dto.ProductListResponseDto;
import com.example.purebasketbe.domain.product.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ProductListResponseDto> getProducts(
            @RequestParam(defaultValue = "1", required = false) int eventPage,
            @RequestParam(defaultValue = "1", required = false) int page) {
        ProductListResponseDto responseBody = productService.getProducts(eventPage - 1, page - 1);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @GetMapping("/search")
    public ResponseEntity<ProductListResponseDto> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "", required = false) String category,
            @RequestParam(defaultValue = "1", required = false) int eventPage,
            @RequestParam(defaultValue = "1", required = false) int page) {
        ProductListResponseDto responseBody = productService.searchProducts(query, category, eventPage - 1, page - 1);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @GetMapping("/search/es")
    public ResponseEntity<ProductListResponseDto> searchProductsByES(
            @RequestParam(defaultValue = "", required = false) String query,
            @RequestParam(defaultValue = "", required = false) String category,
            @RequestParam(defaultValue = "1", required = false) int eventPage,
            @RequestParam(defaultValue = "1", required = false) int page) {
        ProductListResponseDto responseBody = productService.searchProductsByES(query, category, eventPage - 1, page - 1);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long productId) {
        ProductResponseDto responseBody = productService.getProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}
