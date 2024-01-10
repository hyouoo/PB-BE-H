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

    Product product = Product.builder()
            .name("normal product")
            .price(2000)
            .stock(1000)
            .event(Event.NORMAL)
            .discountRate(0)
            .category("product")
            .build();

    @Nested
    @DisplayName("전체 상품 조회")
    class GetProducts {

        int eventPage = 0;
        int page = 0;

        Page<Product> products = getProductsPage(page);

        @BeforeEach
        void setUp() {
            setPrivateFieldOfService();
        }

        @Test
        @DisplayName("조회 성공")
        void getProductsSuccess() {
            // given
            given(productRepository.findAllByDeletedAndEvent(
                    any(Boolean.class), any(Event.class), any(Pageable.class)))
                    .willReturn(products);

            // when
            ProductListResponseDto responseDto = productService.getProducts(eventPage, page);

            // then
            assertThat(responseDto.products().stream()
                    .map(ProductResponseDto::name)
                    .findFirst())
                    .hasValue(product.getName());
        }

        @Test
        @DisplayName("조회 실패(상품 없음)")
        void getProductsFail() {
            // given
            given(productRepository.findAllByDeletedAndEvent(
                    any(Boolean.class), any(Event.class), any(Pageable.class)))
                    .willReturn(Page.empty());

            // when
            ProductListResponseDto responseDto = productService.getProducts(eventPage, page);

            // then
            assertThat(responseDto.eventProducts().getNumberOfElements()).isZero();
            assertThat(responseDto.products().getNumberOfElements()).isZero();
        }
    }

    @Nested
    @DisplayName("상품 검색 조회")
    class searchProducts {

        String query = "event";
        String category = "";
        int eventPage = 0;
        int page = 0;

        Page<Product> products = getProductsPage(page);

        @BeforeEach
        void setUp() {
            setPrivateFieldOfService();
        }

        @Test
        @DisplayName("조회 성공, w/o category")
        void searchProductsSuccessWithoutCategory() {
            // given
            given(productRepository.findAllByDeletedAndEventAndNameContains(
                    any(Boolean.class), any(Event.class), any(String.class), any(Pageable.class)))
                    .willReturn(products);

            // when
            ProductListResponseDto responseDto = productService.searchProducts(query, category, eventPage, page);

            // then
            assertThat(responseDto.products().stream()
                    .map(ProductResponseDto::name)
                    .findFirst())
                    .hasValue(product.getName());
        }

        @Test
        @DisplayName("조회 실패(검색 상품 없음), w/o category")
        void searchProductFailWithoutCategory() {
            // given
            given(productRepository.findAllByDeletedAndEventAndNameContains(
                    any(Boolean.class), any(Event.class), any(String.class), any(Pageable.class)))
                    .willReturn(Page.empty());

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
            category = "test category";

            given(productRepository.findAllByDeletedAndEventAndCategoryAndNameContains(
                    any(Boolean.class), any(Event.class), any(String.class), any(String.class), any(Pageable.class)))
                    .willReturn(products);
            // when
            ProductListResponseDto responseDto = productService.searchProducts(query, category, eventPage, page);

            // then
            assertThat(responseDto.eventProducts().stream()
                    .map(ProductResponseDto::name)
                    .findFirst())
                    .hasValue(product.getName());
        }

        @Test
        @DisplayName("조회 실패(검색 상품 없음), w/ category")
        void searchProductsFailWithCategory() {
            // given
            category = "test category";

            given(productRepository.findAllByDeletedAndEventAndCategoryAndNameContains(
                    any(Boolean.class), any(Event.class), any(String.class), any(String.class), any(Pageable.class)))
                    .willReturn(Page.empty());
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

        Long productId = 1L;

        @Test
        @DisplayName("조회 성공")
        void getProductSuccess() {
            // given
            given(productRepository.findByIdAndDeleted(any(), any(Boolean.class)))
                    .willReturn(Optional.ofNullable(product));
            given(imageRepository.findAllByProductId(any()))
                    .willReturn(List.of());

            // when
            ProductResponseDto responseDto = productService.getProduct(productId);

            // then
            assertThat(responseDto.name()).isEqualTo(product.getName());
        }

        @Test
        @DisplayName("조회 실패")
        void getProductFail() {
            // given
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

        ProductRequestDto requestDto = new ProductRequestDto(
                "test product", 1000, 100, null, null, Event.NORMAL, 0);
        List<MultipartFile> files = List.of(
                new MockMultipartFile("image_name", "image_name.jpg", "image/jpeg", new byte[0]));

        @Test
        @DisplayName("등록 성공")
        void registerProductSuccess() {
            // given
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

        Long productId = 1L;
        ProductRequestDto requestDto = new ProductRequestDto(
                "test update", 1000, 100, null, null, Event.NORMAL, 0);
        List<MultipartFile> files = List.of(
                new MockMultipartFile("image_name", "image_name.jpg", "image/jpeg", new byte[0]));

        @Test
        @DisplayName("수정 성공 w/ files")
        void updateProductWithFilesSuccess() {
            // given
            given(productRepository.findByIdAndDeleted(any(), any(Boolean.class)))
                    .willReturn(Optional.ofNullable(product));

            // when
            productService.updateProduct(productId, requestDto, files);

            // then
            assertThat(product.getName()).isEqualTo(requestDto.name());
        }

        @Test
        @DisplayName("수정 실패(해당 상품 없음) w/ files")
        void updateProductWithFilesFail() {
            // given
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
            files = List.of();

            given(productRepository.findByIdAndDeleted(any(), any(Boolean.class)))
                    .willReturn(Optional.ofNullable(product));

            // when
            productService.updateProduct(productId, requestDto, files);

            // then
            assertThat(product.getName()).isEqualTo(requestDto.name());
        }

        @Test
        @DisplayName("수정 실패(해당 상품 없음) w/o files")
        void updateProductWithoutFilesFail() {
            // given
            files = List.of();

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

        Long productId = 1L;
        List<Image> images = List.of(mock(Image.class));

        @Test
        @DisplayName("삭제 성공")
        void deleteProductSuccess() {
            // given
            given(productRepository.findByIdAndDeleted(any(), any(Boolean.class)))
                    .willReturn(Optional.ofNullable(product));
            given(imageRepository.findAllByProductId(any()))
                    .willReturn(images);

            // when
            productService.deleteProduct(productId);

            // then
            assertThat(product.getName()).startsWith("normal product-deleted-");
            assertThat(product.isDeleted()).isTrue();
            verify(imageRepository, times(1)).findAllByProductId(any());
            verify(s3Handler, times(1)).deleteImage(any());
            verify(imageRepository, times(1)).deleteAllByProductId(any());
        }

        @Test
        @DisplayName("삭제 실패(해당 상품 없음)")
        void deleteProductFail() {
            // given
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

    private Page<Product> getProductsPage(int page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "modifiedAt");
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        List<Product> productList = List.of(product);
        return new PageImpl<>(page == 0 ? productList : List.of(),
                pageable,
                productList.size() / pageSize);
    }
}
