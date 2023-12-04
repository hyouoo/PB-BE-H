package com.example.purebasketbe.domain.product;

import com.example.purebasketbe.domain.product.dto.ProductRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/backoffice")
public class BackOfficeController {

    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> registerProduct(@RequestBody @Validated ProductRequestDto requestDto,
                                                @RequestPart(value = "files") List<MultipartFile> files) {
        productService.registerProduct(requestDto, files);
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header("Location", "/api/backoffice")
                .build();
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateProduct(@PathVariable Long productId,
                                              @RequestBody @Validated ProductRequestDto requestDto,
                                              @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        List<MultipartFile> fileList = Optional.ofNullable(files).orElse(List.of());
        productService.updateProduct(productId, requestDto, fileList);
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header("Location", "/api/backoffice")
                .build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header("Location", "/api/backoffice")
                .build();
    }
}
