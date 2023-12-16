package com.example.purebasketbe.domain.purchase;

import com.example.purebasketbe.domain.cart.CartRepository;
import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.product.ProductRepository;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.purchase.dto.PurchaseRequestDto.PurchaseDetail;
import com.example.purebasketbe.domain.purchase.dto.PurchaseResponseDto;
import com.example.purebasketbe.domain.purchase.entity.Purchase;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

        private List<PurchaseDetail> purchaseRequestDto;
        private Member member;

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
            given(productRepository.findByIdInAndDeleted(anyList(), eq(false))).willReturn(validProductList);

            // when
            purchaseService.purchaseProducts(purchaseRequestDto, member);

            // then
            verify(purchaseRepository, times(1)).saveAll(anyList());
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

            given(productRepository.findByIdInAndDeleted(anyList(), eq(false))).willReturn(validProductList);

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
        private static final int PRODUCTS_PER_PAGE = 20;
        private static final int page = 0;
        private Member member;
        private Product product;
        private Purchase purchase;

        @BeforeEach
        void setUp() {
            member = Member.builder().build();
            product = Product.builder().stock(4).price(1000).discountRate(0).build();
            purchase = Purchase.builder()
                    .member(member)
                    .product(product)
                    .build();
        }

        @DisplayName("주문 내역 조회 성공")
        @ParameterizedTest
        @CsvSource(value = {"desc:purchasedAt", "asc:purchasedAt", "desc:name", "asc:name"}, delimiter = ':')
        void getPurchaseHistorySuccess(String order, String sortBy) {
            // given
            Pageable pageRequest = PageRequest.of(page, PRODUCTS_PER_PAGE, createSort(order, sortBy));
            Page<Purchase> purchases = createPurchasePage(pageRequest);

            given(purchaseRepository.findAllByMember(any(Member.class), any(Pageable.class))).willReturn(purchases);
            given(productRepository.findById(any())).willReturn(Optional.of(product));

            // when
            Page<PurchaseResponseDto> result = purchaseService.getPurchases(member, page, sortBy, order);

            // then
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getTotalPages()).isEqualTo(1);
        }

        @DisplayName("주문 내역 조회 실패")
        @ParameterizedTest
        @CsvSource(value = {"desc:purchasedAt", "asc:purchasedAt", "desc:name", "asc:name"}, delimiter = ':')
        void getPurchaseHistoryFail(String order, String sortBy) {
            // given
            Pageable pageRequest = PageRequest.of(page, PRODUCTS_PER_PAGE, createSort(order, sortBy));
            Page<Purchase> purchases = createPurchasePage(pageRequest);

            given(purchaseRepository.findAllByMember(any(Member.class), any(Pageable.class))).willReturn(purchases);
            given(productRepository.findById(any())).willReturn(Optional.empty());

            // when
            Exception exception = assertThrows(CustomException.class,
                    () -> purchaseService.getPurchases(member, page, sortBy, order));

            // then
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }

        private Sort createSort(String order, String sortBy) {
            Sort.Direction direction = Direction.valueOf(order.toUpperCase());
            return Sort.by(direction, sortBy);
        }

        private Page<Purchase> createPurchasePage(Pageable pageRequest) {
            List<Purchase> purchaseList = new ArrayList<>();
            purchaseList.add(purchase);

            int start = (int) pageRequest.getOffset();
            int end = Math.min((start + pageRequest.getPageSize()), purchaseList.size());
            return new PageImpl<>(purchaseList.subList(start, end), pageRequest,
                    purchaseList.size());
        }
    }
}