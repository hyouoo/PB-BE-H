package com.example.purebasketbe.domain.product;

import com.example.purebasketbe.domain.product.dto.*;
import com.example.purebasketbe.domain.product.entity.Event;
import com.example.purebasketbe.domain.product.entity.Image;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import com.example.purebasketbe.global.s3.S3Handler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final S3Handler s3Handler;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${products.event.page.size}")
    private int eventPageSize;
    @Value("${products.page.size}")
    private int pageSize;
    final String TOPIC = "event";


    @Transactional(readOnly = true)
    public ProductListResponseDto getProducts(int eventPage, int page) {
        Pageable eventPageable = getPageable(eventPage, eventPageSize);
        Pageable pageable = getPageable(page, pageSize);

        Page<Product> eventProducts = productRepository.findAllByDeletedAndEvent(false, Event.DISCOUNT, eventPageable);
        Page<Product> products = productRepository.findAllByDeletedAndEvent(false, Event.NORMAL, pageable);

        Page<ProductResponseDto> eventProductsResponse = getResponseDtoFromProducts(eventProducts);
        Page<ProductResponseDto> productsResponse = getResponseDtoFromProducts(products);

        return ProductListResponseDto.of(eventProductsResponse, productsResponse);
    }

    @Transactional(readOnly = true)
    public ProductListResponseDto searchProducts(String query, String category, int eventPage, int page) {
        Pageable eventPageable = getPageable(eventPage, eventPageSize);
        Pageable pageable = getPageable(page, pageSize);


        Page<Product> eventProducts = category.isEmpty()
                ? productRepository.findAllByDeletedAndEventAndNameContains(
                false, Event.DISCOUNT, query, eventPageable)
                : productRepository.findAllByDeletedAndEventAndCategoryAndNameContains(
                false, Event.DISCOUNT, category, query, eventPageable);

        Page<Product> products = category.isEmpty()
                ? productRepository.findAllByDeletedAndEventAndNameContains(
                false, Event.NORMAL, query, pageable)
                : productRepository.findAllByDeletedAndEventAndCategoryAndNameContains(
                false, Event.NORMAL, category, query, pageable);

        Page<ProductResponseDto> eventProductsResponse = getResponseDtoFromProducts(eventProducts);
        Page<ProductResponseDto> productsResponse = getResponseDtoFromProducts(products);

        return ProductListResponseDto.of(eventProductsResponse, productsResponse);
    }


    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long productId) {
        Product product = findProduct(productId);
        List<String> imageUrlList = getImgUrlList(product);
        return ProductResponseDto.of(product, imageUrlList);
    }

    @Transactional
    public void registerProduct(ProductRequestDto requestDto, List<MultipartFile> files) {
        checkExistProductByName(requestDto.name());
        Product newProduct = Product.from(requestDto);

        productRepository.save(newProduct);
        saveAndUploadImage(newProduct, files);

        if (newProduct.getEvent().equals(Event.DISCOUNT)) {
            kafkaTemplate.send(TOPIC, "New Event Raised");
        }
    }

    @Transactional
    public void updateProduct(Long productId, ProductRequestDto requestDto, List<MultipartFile> files) {
        Product product = findProduct(productId);
        product.update(requestDto);

        if (!files.isEmpty()) {
            saveAndUploadImage(product, files);
        }

        if (product.getEvent().equals(Event.DISCOUNT)) {
            kafkaTemplate.send(TOPIC, "New Event Raised");
        }
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = findProduct(productId);
        product.softDelete();
        imageRepository.findAllByProductId(productId)
                .forEach(image -> s3Handler.deleteImage(image.getImgUrl()));
        imageRepository.deleteAllByProductId(productId);
    }

    private Page<ProductResponseDto> getResponseDtoFromProducts(Page<Product> products) {
        return products.map(product -> {
            List<String> imgUrlList = getImgUrlList(product);
            return ProductResponseDto.of(product, imgUrlList);
        });
    }

    private Pageable getPageable(int page, int pageSize) {
        Sort sort = Sort.by(Sort.Direction.DESC, "modifiedAt");
        return PageRequest.of(page, pageSize, sort);
    }

    private List<String> getImgUrlList(Product product) {
        return imageRepository.findAllByProductId(product.getId())
                .stream()
                .map(Image::getImgUrl)
                .toList();
    }

    private void saveAndUploadImage(Product product, List<MultipartFile> files) {
        List<Image> imageList = new ArrayList<>();
        List<String> imgUrlList = new ArrayList<>();
        for (MultipartFile file : files) {
            String imgUrl = s3Handler.makeUrl(file);
            Image image = Image.of(imgUrl, product);
            imageList.add(image);
            imgUrlList.add(imgUrl);
        }
        imageRepository.saveAll(imageList);
        s3Handler.uploadImages(imgUrlList, files);
    }

    private void checkExistProductByName(String name) {
        if (productRepository.existsByName(name)) {
            throw new CustomException(ErrorCode.PRODUCT_ALREADY_EXISTS);
        }
    }

    private Product findProduct(Long id) {
        return productRepository.findByIdAndDeleted(id, false).orElseThrow(
                () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
        );
    }
}