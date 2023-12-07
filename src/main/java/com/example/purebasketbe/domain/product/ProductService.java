package com.example.purebasketbe.domain.product;

import com.example.purebasketbe.domain.product.dto.*;
import com.example.purebasketbe.domain.product.entity.Event;
import com.example.purebasketbe.domain.product.entity.Image;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final S3Template s3Template;

    @Value("${products.event.page.size}")
    private int eventPageSize;
    @Value("${products.page.size}")
    private int pageSize;
    @Value("${aws.bucket.name}")
    private String bucket;
    @Value("${spring.cloud.aws.region.static}")
    private String region;


    @Transactional(readOnly = true)
    public ProductListResponseDto getProducts(int eventPage, int page) {
        Pageable eventPageable = getPageable(eventPage, eventPageSize);
        Page<Product> eventProducts = productRepository.findAllByDeletedAndEvent(false, Event.DISCOUNT, eventPageable);
        Page<ProductResponseDto> eventProductsResponse = eventProducts.map(ProductResponseDto::from);
        List<ImageResponseDto> eventImageUrlResponse = getImageUrlResponse(eventProducts);

        Pageable pageable = getPageable(page, pageSize);
        Page<Product> products = productRepository.findAllByDeletedAndEvent(false, Event.NORMAL, pageable);
        Page<ProductResponseDto> productsResponse = products.map(ProductResponseDto::from);
        List<ImageResponseDto> imageUrlResponse = getImageUrlResponse(products);

        return ProductListResponseDto.of(eventProductsResponse, eventImageUrlResponse, productsResponse, imageUrlResponse);
    }

    @Transactional(readOnly = true)
    public ProductListResponseDto searchProducts(String query, String category, int eventPage, int page) {
        Pageable eventPageable = getPageable(eventPage, eventPageSize);
        Pageable pageable = getPageable(page, pageSize);

        Page<Product> eventProducts;
        Page<Product> products;
        if (category.isEmpty()) {
            eventProducts = productRepository.findAllByDeletedAndEventAndNameContains(
                    false, Event.DISCOUNT, query, eventPageable);
            products = productRepository.findAllByDeletedAndEventAndNameContains(
                    false, Event.NORMAL, query, pageable);
        } else {
            eventProducts = productRepository.findAllByDeletedAndEventAndCategoryAndNameContains(
                    false, Event.DISCOUNT, category, query, eventPageable);
            products = productRepository.findAllByDeletedAndEventAndCategoryAndNameContains(
                    false, Event.NORMAL, category, query, pageable);
        }
        Page<ProductResponseDto> eventProductsResponse = eventProducts.map(ProductResponseDto::from);
        List<ImageResponseDto> eventImageUrlResponse = getImageUrlResponse(eventProducts);
        Page<ProductResponseDto> productsResponse = products.map(ProductResponseDto::from);
        List<ImageResponseDto> imageUrlResponse = getImageUrlResponse(products);

        return ProductListResponseDto.of(eventProductsResponse, eventImageUrlResponse, productsResponse, imageUrlResponse);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponseDto getProduct(Long productId) {
        Product product = findProduct(productId);
        List<String> imageUrlList = getImgUrlList(product);
        return ProductDetailResponseDto.of(product, imageUrlList);
    }

    @Transactional
    public void registerProduct(ProductRequestDto requestDto, List<MultipartFile> files) {
        checkExistProductByName(requestDto.name());
        Product newProduct = Product.from(requestDto);
        productRepository.save(newProduct);

        for (MultipartFile file : files) {
            saveAndUploadImage(file, newProduct);
        }
    }

    @Transactional
    public void updateProduct(Long productId, ProductRequestDto requestDto, List<MultipartFile> files) {
        Product product = findProduct(productId);
        product.update(requestDto);

        if (!files.isEmpty()) {
            for (MultipartFile file : files) {
                saveAndUploadImage(file, product);
            }
        }
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = findProduct(productId);
        product.softDelete();
        imageRepository.findAllByProductId(productId)
                .forEach(image -> s3Template.deleteObject(bucket, getKey(image.getImgUrl())));
        imageRepository.deleteAllByProductId(productId);
    }

    private Pageable getPageable(int page, int pageSize) {
        Sort sort = Sort.by(Sort.Direction.DESC, "modifiedAt");
        return PageRequest.of(page, pageSize, sort);
    }

    private List<ImageResponseDto> getImageUrlResponse(Page<Product> products) {
        List<ImageResponseDto> imageUrlResponse = new ArrayList<>();
        for (Product product : products) {
            List<String> imgUrlList = getImgUrlList(product);
            imageUrlResponse.add(ImageResponseDto.of(product, imgUrlList));
        }
        return imageUrlResponse;
    }

    private List<String> getImgUrlList(Product product) {
        return imageRepository.findAllByProductId(product.getId())
                .stream()
                .map(Image::getImgUrl)
                .toList();
    }

    private void saveAndUploadImage(MultipartFile file, Product product) {
        String key = UUID.randomUUID() + "_" + file.getOriginalFilename();
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INVALID_IMAGE);
        }
        ObjectMetadata metadata = ObjectMetadata.builder().contentType("text/plain").build();

        String publicUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);
        Image newimage = Image.of(publicUrl, product);
        imageRepository.save(newimage);

        s3Template.upload(bucket, key, inputStream, metadata);
    }

    private String getKey(String imgUrl) {
        int lastIndex = imgUrl.lastIndexOf("/");
        if (lastIndex != -1 && lastIndex < imgUrl.length() - 1) {
            return imgUrl.substring(lastIndex + 1);
        } else throw new CustomException(ErrorCode.INVALID_IMAGE);
    }

    private void checkExistProductByName(String name) {
        if (productRepository.existsByName(name))
            throw new CustomException(ErrorCode.PRODUCT_ALREADY_EXISTS);
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
        );
    }
}