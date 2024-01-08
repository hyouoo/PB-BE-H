package com.example.purebasketbe.domain.product;

import com.example.purebasketbe.domain.product.dto.ProductListResponseDto;
import com.example.purebasketbe.domain.product.dto.ProductRequestDto;
import com.example.purebasketbe.domain.product.dto.ProductResponseDto;
import com.example.purebasketbe.domain.product.entity.*;
import com.example.purebasketbe.global.RestPageImpl;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import com.example.purebasketbe.global.kafka.KafkaService;
import com.example.purebasketbe.global.s3.S3Handler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final StockRepository stockRepository;
    private final S3Handler s3Handler;
    private final KafkaService kafkaHandler;
    private final ElasticsearchOperations elasticsearchOperations;

    @Value("${products.event.page.size}")
    private int eventPageSize;
    @Value("${products.page.size}")
    private int pageSize;

    @Cacheable(value = "products", key = "#eventPage + '_' + #page")
    @Transactional(readOnly = true)
    public ProductListResponseDto getProducts(int eventPage, int page) {
        Pageable eventPageable = getPageable(eventPage, eventPageSize);
        Pageable pageable = getPageable(page, pageSize);


        Page<Product> eventProducts = productRepository.findAllByDeletedAndEvent(false, Event.DISCOUNT, eventPageable);
        Page<Product> products = productRepository.findAllByDeletedAndEvent(false, Event.NORMAL, pageable);

        RestPageImpl<ProductResponseDto> eventProductsRestPage = RestPageImpl.from(getResponseDtoFromProducts(eventProducts));
        RestPageImpl<ProductResponseDto> productsRestPage = RestPageImpl.from(getResponseDtoFromProducts(products));

        return ProductListResponseDto.of(eventProductsRestPage, productsRestPage);
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
    public ProductListResponseDto searchProductsByES(String query, String category, int eventPage, int page) {
        Pageable eventPageable = getPageable(eventPage, eventPageSize);
        Pageable pageable = getPageable(page, pageSize);

        Criteria eventCriteria;
        if (category.isEmpty()) {
            eventCriteria = new Criteria("name").contains(query).and("event").is("DISCOUNT");
        } else {
            eventCriteria = new Criteria("name").contains(query).and("event").is("DISCOUNT").and("category").is(category);
        }
        SearchPage<ProductDocument> eventProducts = getPagedSearchResults(eventCriteria, eventPageable);

        Criteria criteria;
        if (category.isEmpty()) {
            criteria = new Criteria("name").contains(query).and("event").is("NORMAL");
        } else {
            criteria = new Criteria("name").contains(query).and("event").is("NORMAL").and("category").is(category);
        }
        SearchPage<ProductDocument> products = getPagedSearchResults(criteria, pageable);

        Page<ProductResponseDto> eventProductsResponse = eventProducts.map(ProductResponseDto::from);
        Page<ProductResponseDto> productsResponse = products.map(ProductResponseDto::from);

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
        Stock stock = Stock.of(requestDto, newProduct);
        newProduct.attachStock(stock);

        productRepository.save(newProduct);
        stockRepository.save(stock);
        saveAndUploadImage(newProduct, files);

        elasticsearchOperations.save(ProductDocument.from(newProduct));

        if (newProduct.getEvent().equals(Event.DISCOUNT)) {
            kafkaHandler.sendEventToKafka(ProductResponseDto.from(newProduct));
        }
    }

    @Transactional
    public void updateProduct(Long productId, ProductRequestDto requestDto, List<MultipartFile> files) {
        Product product = findProduct(productId);
        Stock stock = product.getStock();
        product.update(requestDto);

        if (requestDto.stock() != null) {
            stock.update(requestDto.stock());
        }

        if (!files.isEmpty()) {
            saveAndUploadImage(product, files);
        }

        if (product.getEvent().equals(Event.DISCOUNT)) {
            kafkaHandler.sendEventToKafka(ProductResponseDto.from(product));
        }
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = findProduct(productId);
        product.softDelete();
        List<Image> imageList = imageRepository.findAllByProductId(productId);
        imageRepository.deleteAllByProductId(productId);
        imageList.forEach(image -> s3Handler.deleteImage(image.getImgUrl()));
    }

    private Page<ProductResponseDto> getResponseDtoFromProducts(Page<Product> products) {
        return products.map(product -> {
            List<String> imgUrlList = getImgUrlList(product);
            return ProductResponseDto.of(product, imgUrlList);
        });
    }

    private SearchPage<ProductDocument> getPagedSearchResults(Criteria criteria, Pageable pageable) {
        Query searchQuery = new CriteriaQuery(criteria);
        SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(searchQuery, ProductDocument.class);
        return SearchHitSupport.searchPageFor(searchHits, pageable);
//        SearchPage<ProductDocument> searchPage = SearchHitSupport.searchPageFor(searchHits, pageable);
//        return (Page<ProductDocument>) SearchHitSupport.unwrapSearchHits(searchPage);
    }

    private Pageable getPageable(int page, int pageSize) {
        Sort sort = Sort.by(Sort.Direction.DESC, "modifiedAt");
        return PageRequest.of(page, pageSize, sort);
    }

    private List<String> getImgUrlList(Product product) {
        return product.getImages().stream().map(Image::getImgUrl).toList();
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