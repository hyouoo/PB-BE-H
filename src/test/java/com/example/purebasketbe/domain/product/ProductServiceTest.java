package com.example.purebasketbe.domain.product;

import com.example.purebasketbe.domain.product.dto.ProductListResponseDto;
import com.example.purebasketbe.domain.product.dto.ProductRequestDto;
import com.example.purebasketbe.domain.product.dto.ProductResponseDto;
import com.example.purebasketbe.domain.product.entity.Event;
import com.example.purebasketbe.domain.product.entity.Image;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import com.example.purebasketbe.global.s3.S3Handler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    ProductRepository productRepository;
    @Mock
    ImageRepository imageRepository;
    @Mock
    S3Handler s3Handler;
    @InjectMocks
    ProductService productService;

    int eventPageSize = 4;
    int pageSize = 12;

    int eventPage;
    int page;

    Product eventProduct;
    Product normalProduct;

    @BeforeEach
    void setProducts() {
        eventProduct = Product.builder()
                .name("event product")
                .price(1000)
                .stock(100)
                .event(Event.DISCOUNT)
                .discountRate(50)
                .category("product")
                .build();

        normalProduct = Product.builder()
                .name("normal product")
                .price(2000)
                .stock(1000)
                .event(Event.NORMAL)
                .discountRate(0)
                .category("product")
                .build();
    }

    @Nested
    @DisplayName("전체 상품 조회")
    class GetProducts {

        @Test
        @DisplayName("조회 성공")
        void getProductsSuccess() {
            // given
            eventPage = 0;
            page = 0;

            setPrivateFieldOfService();
            List<Pageable> pageables = getPageables(eventPage, page);
            List<Page<Product>> productsPages = getProductsPages(eventPage, page, pageables);

            given(productRepository.findAllByDeletedAndEvent(false, Event.DISCOUNT, pageables.get(0)))
                    .willReturn(productsPages.get(0));
            given(productRepository.findAllByDeletedAndEvent(false, Event.NORMAL, pageables.get(1)))
                    .willReturn(productsPages.get(1));

            // when
            ProductListResponseDto responseDto = productService.getProducts(eventPage, page);

            // then
            assertThat(responseDto.eventProducts().stream()
                    .map(ProductResponseDto::name)
                    .findFirst())
                    .hasValue(eventProduct.getName());
            assertThat(responseDto.products().stream()
                    .map(ProductResponseDto::name)
                    .findFirst())
                    .hasValue(normalProduct.getName());
        }

        @Test
        @DisplayName("조회 실패")
        void getProductsFail() {
            // given
            eventPage = 2;
            page = 0;

            setPrivateFieldOfService();
            List<Pageable> pageables = getPageables(eventPage, page);
            List<Page<Product>> productsPages = getProductsPages(eventPage, page, pageables);

            given(productRepository.findAllByDeletedAndEvent(false, Event.DISCOUNT, pageables.get(0)))
                    .willReturn(productsPages.get(0));
            given(productRepository.findAllByDeletedAndEvent(false, Event.NORMAL, pageables.get(1)))
                    .willReturn(productsPages.get(1));

            // when
            ProductListResponseDto responseDto = productService.getProducts(eventPage, page);

            // then
            assertThat(responseDto.eventProducts().getNumberOfElements()).isZero();
        }
    }

    @Nested
    @DisplayName("상품 검색 조회")
    class searchProducts {

        @Test
        @DisplayName("조회 성공, w/o category")
        void searchProductsSuccessWithoutCategory() {
            // given
            String query = "event";
            String category = "";
            eventPage = 0;
            page = 0;
            setPrivateFieldOfService();
            List<Pageable> pageables = getPageables(eventPage, page);
            List<Page<Product>> productsPages = getProductsPages(eventPage, page, pageables);

            when(productRepository.findAllByDeletedAndEventAndNameContains(
                    false, Event.DISCOUNT, query, pageables.get(0)))
                    .thenAnswer(invocation -> {
                        if (eventProduct.getName().contains(query)) {
                            return productsPages.get(0);
                        } else {
                            return emptyPage(pageables.get(0));
                        }
                    });
            when(productRepository.findAllByDeletedAndEventAndNameContains(
                    false, Event.NORMAL, query, pageables.get(1)))
                    .thenAnswer(invocation -> {
                        if (normalProduct.getName().contains(query)) {
                            return productsPages.get(1);
                        } else {
                            return emptyPage(pageables.get(1));
                        }
                    });

            // when
            ProductListResponseDto responseDto = productService.searchProducts(query, category, eventPage, page);

            // then
            assertThat(responseDto.eventProducts().stream()
                    .map(ProductResponseDto::name)
                    .findFirst())
                    .hasValue(eventProduct.getName());
            assertThat(responseDto.products().getNumberOfElements()).isZero();
        }

        @Test
        @DisplayName("조회 실패(검색 상품 없음), w/o category")
        void searchProductFailWithoutCategory() {
            // given
            String query = "nothing";
            String category = "";
            eventPage = 0;
            page = 0;
            setPrivateFieldOfService();
            List<Pageable> pageables = getPageables(eventPage, page);
            List<Page<Product>> productsPages = getProductsPages(eventPage, page, pageables);

            when(productRepository.findAllByDeletedAndEventAndNameContains(
                    false, Event.DISCOUNT, query, pageables.get(0)))
                    .thenAnswer(invocation -> {
                        if (eventProduct.getName().contains(query)) {
                            return productsPages.get(0);
                        } else {
                            return emptyPage(pageables.get(0));
                        }
                    });
            when(productRepository.findAllByDeletedAndEventAndNameContains(
                    false, Event.NORMAL, query, pageables.get(1)))
                    .thenAnswer(invocation -> {
                        if (normalProduct.getName().contains(query)) {
                            return productsPages.get(1);
                        } else {
                            return emptyPage(pageables.get(1));
                        }
                    });

            // when
            ProductListResponseDto responseDto = productService.searchProducts(query, category, eventPage, page);

            // then
            assertThat(responseDto.eventProducts().getNumberOfElements()).isZero();
            assertThat(responseDto.products().getNumberOfElements()).isZero();
        }

        @Test
        @DisplayName("조회 성공, w/ category")
        void searchProductsSuccessWithCategory() {
            // given
            String query = "event";
            String category = "product";
            eventPage = 0;
            page = 0;
            setPrivateFieldOfService();
            List<Pageable> pageables = getPageables(eventPage, page);
            List<Page<Product>> productsPages = getProductsPages(eventPage, page, pageables);

            when(productRepository.findAllByDeletedAndEventAndCategoryAndNameContains(
                    false, Event.DISCOUNT, category, query, pageables.get(0)))
                    .thenAnswer(invocation -> {
                        if (eventProduct.getName().contains(query) && eventProduct.getCategory().equals(category)) {
                            return productsPages.get(0);
                        } else {
                            return emptyPage(pageables.get(0));
                        }
                    });
            when(productRepository.findAllByDeletedAndEventAndCategoryAndNameContains(
                    false, Event.NORMAL, category, query, pageables.get(1)))
                    .thenAnswer(invocation -> {
                        if (normalProduct.getName().contains(query) && normalProduct.getCategory().equals(category)) {
                            return productsPages.get(1);
                        } else {
                            return emptyPage(pageables.get(1));
                        }
                    });

            // when
            ProductListResponseDto responseDto = productService.searchProducts(query, category, eventPage, page);

            // then
            assertThat(responseDto.eventProducts().stream()
                    .map(ProductResponseDto::name)
                    .findFirst())
                    .hasValue(eventProduct.getName());
            assertThat(responseDto.products().getNumberOfElements()).isZero();
        }

        @Test
        @DisplayName("조회 실패(검색 상품 없음), w/ category")
        void searchProductsFailWithCategory() {
            // given
            String query = "event";
            String category = "nothing";
            eventPage = 0;
            page = 0;
            setPrivateFieldOfService();
            List<Pageable> pageables = getPageables(eventPage, page);
            List<Page<Product>> productsPages = getProductsPages(eventPage, page, pageables);

            when(productRepository.findAllByDeletedAndEventAndCategoryAndNameContains(
                    false, Event.DISCOUNT, category, query, pageables.get(0)))
                    .thenAnswer(invocation -> {
                        if (eventProduct.getName().contains(query) && eventProduct.getCategory().equals(category)) {
                            return productsPages.get(0);
                        } else {
                            return emptyPage(pageables.get(0));
                        }
                    });
            when(productRepository.findAllByDeletedAndEventAndCategoryAndNameContains(
                    false, Event.NORMAL, category, query, pageables.get(1)))
                    .thenAnswer(invocation -> {
                        if (normalProduct.getName().contains(query) && normalProduct.getCategory().equals(category)) {
                            return productsPages.get(1);
                        } else {
                            return emptyPage(pageables.get(1));
                        }
                    });

            // when
            ProductListResponseDto responseDto = productService.searchProducts(query, category, eventPage, page);

            // then
            assertThat(responseDto.eventProducts().getNumberOfElements()).isZero();
            assertThat(responseDto.products().getNumberOfElements()).isZero();
        }
    }

    @Nested
    @DisplayName("단일 상품 조회")
    class GetProduct {

        @Test
        @DisplayName("조회 성공")
        void getProductSuccess() {
            // given
            Long productId = eventProduct.getId();

            given(productRepository.findByIdAndDeleted(productId, false))
                    .willReturn(Optional.ofNullable(eventProduct));
            given(imageRepository.findAllByProductId(productId))
                    .willReturn(List.of());

            // when
            ProductResponseDto responseDto = productService.getProduct(productId);

            // then
            assertThat(responseDto.name()).isEqualTo(eventProduct.getName());
        }

        @Test
        @DisplayName("조회 실패")
        void getProductFail() {
            // given
            Long productId = 3L;

            given(productRepository.findByIdAndDeleted(any(), any(Boolean.class)))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productService.getProduct(productId))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("상품 등록")
    class RegisterProduct {

        @Test
        @DisplayName("등록 성공")
        void registerProductSuccess() {
            // given
            ProductRequestDto requestDto = new ProductRequestDto(
                    "test product", 1000, 100, null, null, Event.NORMAL, 0
            );
            List<MultipartFile> files = List.of(
                    new MockMultipartFile("image_name", "image_name.jpg", "image/jpeg", new byte[0]));

            given(productRepository.existsByName(any())).willReturn(false);

            // when
            productService.registerProduct(requestDto, files);

            // then
            verify(productRepository, times(1)).save(any());
            verify(imageRepository, times(1)).saveAll(any());
            verify(s3Handler, times(1)).uploadImages(anyList(), anyList());
        }

        @Test
        @DisplayName("등록 실패(중복 상품)")
        void registerProductFail() {
            // given
            ProductRequestDto requestDto = new ProductRequestDto(
                    "test product", 1000, 100, null, null, Event.NORMAL, 0
            );
            List<MultipartFile> files = List.of(
                    new MockMultipartFile("image_name", "image_name.jpg", "image/jpeg", new byte[0]));

            given(productRepository.existsByName(any())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> productService.registerProduct(requestDto, files))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.PRODUCT_ALREADY_EXISTS.getMessage());
            verify(productRepository, never()).save(any());
            verify(imageRepository, never()).saveAll(any());
            verify(s3Handler, never()).uploadImages(anyList(), anyList());
        }
    }

    @Nested
    @DisplayName("상품 수정")
    class UpdateProduct {

        @Test
        @DisplayName("수정 성공 w/ files")
        void updateProductWithFilesSuccess() {
            // given
            Long productId = 1L;
            ProductRequestDto requestDto = new ProductRequestDto(
                    "test update", 1000, 100, null, null, Event.NORMAL, 0
            );
            List<MultipartFile> files = List.of(
                    new MockMultipartFile("image_name", "image_name.jpg", "image/jpeg", new byte[0]));

            given(productRepository.findByIdAndDeleted(any(), any(Boolean.class)))
                    .willReturn(Optional.ofNullable(normalProduct));

            // when
            productService.updateProduct(productId, requestDto, files);

            // then
            assertThat(normalProduct.getName()).isEqualTo(requestDto.name());
        }

        @Test
        @DisplayName("수정 실패(해당 상품 없음) w/ files")
        void updateProductWithFilesFail() {
            // given
            Long productId = 1L;
            ProductRequestDto requestDto = new ProductRequestDto(
                    "test update", 1000, 100, null, null, Event.NORMAL, 0
            );
            List<MultipartFile> files = List.of(
                    new MockMultipartFile("image_name", "image_name.jpg", "image/jpeg", new byte[0]));

            given(productRepository.findByIdAndDeleted(any(), any(Boolean.class)))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productService.updateProduct(productId, requestDto, files))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("수정 성공 w/o files")
        void updateProductWithoutFilesSuccess() {
            // given
            Long productId = 1L;
            ProductRequestDto requestDto = new ProductRequestDto(
                    "test update", 1000, 100, null, null, Event.NORMAL, 0
            );
            List<MultipartFile> files = List.of();

            given(productRepository.findByIdAndDeleted(any(), any(Boolean.class)))
                    .willReturn(Optional.ofNullable(normalProduct));

            // when
            productService.updateProduct(productId, requestDto, files);

            // then
            assertThat(normalProduct.getName()).isEqualTo(requestDto.name());
        }

        @Test
        @DisplayName("수정 실패(해당 상품 없음) w/o files")
        void updateProductWithoutFilesFail() {
            // given
            Long productId = 1L;
            ProductRequestDto requestDto = new ProductRequestDto(
                    "test update", 1000, 100, null, null, Event.NORMAL, 0
            );
            List<MultipartFile> files = List.of();

            given(productRepository.findByIdAndDeleted(any(), any(Boolean.class)))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productService.updateProduct(productId, requestDto, files))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("상품 삭제")
    class DeleteProduct {

        @Test
        @DisplayName("삭제 성공")
        void deleteProductSuccess() {
            // given
            Long productId = 1L;
            List<Image> images = List.of(mock(Image.class));

            given(productRepository.findByIdAndDeleted(any(), any(Boolean.class)))
                    .willReturn(Optional.ofNullable(normalProduct));
            given(imageRepository.findAllByProductId(any()))
                    .willReturn(images);

            // when
            productService.deleteProduct(productId);

            // then
            assertThat(normalProduct.getName()).startsWith("normal product-deleted-");
            assertThat(normalProduct.isDeleted()).isTrue();
            verify(imageRepository, times(1)).findAllByProductId(any());
            verify(s3Handler, times(1)).deleteImage(any());
            verify(imageRepository, times(1)).deleteAllByProductId(any());
        }

        @Test
        @DisplayName("삭제 실패(해당 상품 없음)")
        void deleteProductFail() {
            // given
            Long productId = 1L;

            given(productRepository.findByIdAndDeleted(any(), any(Boolean.class)))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productService.deleteProduct(productId))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }
    }

    private void setPrivateFieldOfService() {
        ReflectionTestUtils.setField(productService, "eventPageSize", eventPageSize);
        ReflectionTestUtils.setField(productService, "pageSize", pageSize);
    }

    private List<Pageable> getPageables(int eventPage, int page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "modifiedAt");
        Pageable eventPageable = PageRequest.of(eventPage, eventPageSize, sort);
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        return List.of(eventPageable, pageable);
    }

    private List<Page<Product>> getProductsPages(int eventPage, int page, List<Pageable> pageables) {
        List<Product> eventProductList = List.of(eventProduct);
        List<Product> normalProductList = List.of(normalProduct);
        Page<Product> eventProducts = new PageImpl<>(eventPage == 0 ? eventProductList : List.of(), pageables.get(0), eventProductList.size() / eventPageSize);
        Page<Product> products = new PageImpl<>(page == 0 ? normalProductList : List.of(), pageables.get(1), normalProductList.size() / pageSize);

        return List.of(eventProducts, products);
    }

    private Page<Product> emptyPage(Pageable pageable) {
        return new PageImpl<>(List.of(), pageable, 1);
    }

    private void printJsonResult(Object responseDto) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonResponse = mapper.writeValueAsString(responseDto);
            System.out.println(jsonResponse);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
