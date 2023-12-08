package com.example.purebasketbe.domain.product;

import com.example.purebasketbe.domain.product.dto.ProductListResponseDto;
import com.example.purebasketbe.domain.product.dto.ProductRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@PreAuthorize("!hasAuthority('ROLE_MEMBER')")
@RequestMapping("/api/backoffice/products")
public class BackOfficeProductController {

    private final ProductService productService;
    private final String adminLandingPath = "/api/backoffice/products";

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> registerProduct(@RequestPart(value = "dto") @Validated ProductRequestDto requestDto,
                                                @RequestPart(value = "files") List<MultipartFile> files) {
        productService.registerProduct(requestDto, files);
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header("Location", adminLandingPath)
                .build();
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateProduct(@PathVariable Long productId,
                                              @RequestPart(value = "dto") @Validated ProductRequestDto requestDto,
                                              @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        List<MultipartFile> fileList = Optional.ofNullable(files).orElse(List.of());
        productService.updateProduct(productId, requestDto, fileList);
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header("Location", adminLandingPath)
                .build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header("Location", adminLandingPath)
                .build();
    }
}
