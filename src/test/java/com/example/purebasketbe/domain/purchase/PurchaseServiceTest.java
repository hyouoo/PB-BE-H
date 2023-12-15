package com.example.purebasketbe.domain.purchase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.purebasketbe.domain.cart.CartRepository;
import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.product.ProductRepository;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.purchase.dto.PurchaseResponseDto;
import com.example.purebasketbe.domain.purchase.dto.PurchaseRequestDto.PurchaseDetail;
import com.example.purebasketbe.domain.purchase.entity.Purchase;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    PurchaseRepository purchaseRepository;
    @Mock
    CartRepository cartRepository;
    @Mock
    ProductRepository productRepository;
    @InjectMocks
    PurchaseService purchaseService;


    @Nested
    @DisplayName("주문")
    class PurchaseProducts {

        private Member member;
        private List<PurchaseDetail> purchaseRequestDto;

        @BeforeEach
        void setUp() {
            member = Member.builder().build();
            purchaseRequestDto = List.of(
                    PurchaseDetail.builder().productId(1L).amount(2).build(),
                    PurchaseDetail.builder().productId(2L).amount(3).build()
            );
        }

        @Test
        @DisplayName("주문 성공")
        void purchaseProductsSuccess() {
            // given
            List<Product> validProductList = prepareValidProductList();
            given(productRepository.findByIdInAndDeleted(any(), eq(false))).willReturn(validProductList);

            // when
            purchaseService.purchaseProducts(purchaseRequestDto, member);

            // then
            verify(purchaseRepository, times(1)).saveAll(any());
            verify(cartRepository, times(1)).deleteByUserAndProductIn(member, validProductList);

        }

        @Test
        @DisplayName("주문 실패 - 존재하지 않는 상품")
        void purchaseProductsFail1() {
            // given

            // when
            Exception exception = assertThrows(CustomException.class, () ->
                    purchaseService.purchaseProducts(purchaseRequestDto, member)
            );

            // then
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("주문 실패 - 재고 부족")
        void purchaseProductsFail2() {
            // given
            Product product1 = Product.builder().stock(1).price(1000).discountRate(0).build();
            Product product2 = Product.builder().stock(1).price(1000).discountRate(0).build();
            List<Product> validProductList = List.of(product1, product2);

            given(productRepository.findByIdInAndDeleted(any(), eq(false))).willReturn(validProductList);

            // when
            Exception exception = assertThrows(CustomException.class, () ->
                    purchaseService.purchaseProducts(purchaseRequestDto, member)
            );

            // then
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.NOT_ENOUGH_PRODUCT.getMessage());
        }

        private List<Product> prepareValidProductList() {
            return List.of(
                    Product.builder().stock(4).price(1000).discountRate(0).build(),
                    Product.builder().stock(5).price(10000).discountRate(0).build()
            );
        }
    }

    @Nested
    @DisplayName("주문 내역 조회")
    class GetPurchaseHistory {
        @DisplayName("주문 내역 조회 성공")
        @ParameterizedTest
        @CsvSource(value = {"desc:purchasedAt", "asc:purchasedAt", "desc:name", "asc:name"}, delimiter = ':')
        void getPurchaseHistorySuccess(String order, String sortBy) {
            // given
            int PRODUCTS_PER_PAGE = 20;
            int page = 0;

            Member member = Member.builder().build();
            Sort.Direction direction = Direction.valueOf(order.toUpperCase());
            Sort sort = Sort.by(direction, sortBy);
            Pageable pageRequest = PageRequest.of(page, PRODUCTS_PER_PAGE, sort);

            Product product = Product.builder().stock(4).price(1000).discountRate(0).build();
            List<Purchase> purchaseList = new ArrayList<>();
            for (int i = 0; i < PRODUCTS_PER_PAGE * 2; i++) {
                Purchase purchase = Purchase.builder()
                        .member(member)
                        .product(product)
                        .build();
                purchaseList.add(purchase);
            }

            int start = (int) pageRequest.getOffset();
            int end = Math.min((start + pageRequest.getPageSize()), purchaseList.size());
            Page<Purchase> purchases = new PageImpl<>(purchaseList.subList(start, end), pageRequest,
                    purchaseList.size());

            given(purchaseRepository.findAllByMember(any(), any())).willReturn(purchases);
            given(productRepository.findById(any())).willReturn(Optional.of(product));

            // when
            Page<PurchaseResponseDto> result = purchaseService.getPurchases(member, page, sortBy, order);

            // then
            assertThat(result.getTotalPages()).isEqualTo(2);
            assertThat(result.getTotalElements()).isEqualTo(PRODUCTS_PER_PAGE * 2);
        }

        @DisplayName("주문 내역 조회 실패")
        @ParameterizedTest
        @CsvSource(value = {"desc:purchasedAt", "asc:purchasedAt", "desc:name", "asc:name"}, delimiter = ':')
        void getPurchaseHistoryFail(String order, String sortBy) {
            // given
            int PRODUCTS_PER_PAGE = 20;
            int page = 0;

            Member member = Member.builder().build();
            Sort.Direction direction = Direction.valueOf(order.toUpperCase());
            Sort sort = Sort.by(direction, sortBy);
            Pageable pageRequest = PageRequest.of(page, PRODUCTS_PER_PAGE, sort);

            Product product = Product.builder().stock(4).price(1000).discountRate(0).build();
            List<Purchase> purchaseList = new ArrayList<>();
            Purchase purchase = Purchase.builder()
                    .member(member)
                    .product(product)
                    .build();
            purchaseList.add(purchase);

            Page<Purchase> purchases = new PageImpl<>(purchaseList, pageRequest,
                    purchaseList.size());

            given(purchaseRepository.findAllByMember(any(), any())).willReturn(purchases);
            given(productRepository.findById(any())).willReturn(Optional.empty());

            // when
            Exception exception = assertThrows(CustomException.class,
                    () -> purchaseService.getPurchases(member, page, sortBy, order));

            // then
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }

    }
}