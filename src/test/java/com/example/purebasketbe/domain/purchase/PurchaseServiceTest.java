package com.example.purebasketbe.domain.purchase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

        @Test
        @DisplayName("주문 성공")
        void purchaseProductsSuccess() {
            // given
            Product product1 = Product.builder().build();
            Product product2 = Product.builder().build();
            PurchaseDetail purchaseDetail1 = PurchaseDetail.builder().productId(1L).amount(2).build();
            PurchaseDetail purchaseDetail2 = PurchaseDetail.builder().productId(2L).amount(3).build();
            List<PurchaseDetail> purchaseRequestDto = List.of(purchaseDetail1, purchaseDetail2);

//            given(productRepository.findByIdIn(any())).willReturn()

            // when

            // then
        }
        @Test
        @DisplayName("주문 실패")
        void purchaseProductsFail() {
            // given

            // when

            // then
        }
    }

    @Nested
    @DisplayName("주문 내역 조회")
    class GetPurchaseHistory {
        @DisplayName("주문 내역 조회 성공")
        @ParameterizedTest
        @CsvSource(value = {"desc:purchasedAt", "asc:purchasedAt", "desc:name", "asc:name"}, delimiter=':')
        void getPurchaseHistorySuccess(String order, String sortBy) {
            // given
            int PRODUCTS_PER_PAGE = 20;
            int page = 0;

            Member member = Member.builder().build();
            Sort.Direction direction = Direction.valueOf(order.toUpperCase());
            Sort sort = Sort.by(direction, sortBy);
            Pageable pageRequest = PageRequest.of(page, PRODUCTS_PER_PAGE, sort);

            Product product = Product.builder().build();
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

            given(purchaseRepository.findAllByMember(member, pageRequest)).willReturn(purchases);
            given(productRepository.findById(any())).willReturn(Optional.of(product));

            // when
            Page<PurchaseResponseDto> result = purchaseService.getPurchases(member, page, sortBy, order);

            // then
            assertThat(result.getTotalPages()).isEqualTo(2);
            assertThat(result.getTotalElements()).isEqualTo(PRODUCTS_PER_PAGE * 2);
        }

        @DisplayName("주문 내역 조회 실패")
        @ParameterizedTest
        @CsvSource(value = {"desc:purchasedAt", "asc:purchasedAt", "desc:name", "asc:name"}, delimiter=':')
        void getPurchaseHistoryFail(String order, String sortBy) {
            // given
            int PRODUCTS_PER_PAGE = 20;
            int page = 0;

            Member member = Member.builder().build();
            Sort.Direction direction = Direction.valueOf(order.toUpperCase());
            Sort sort = Sort.by(direction, sortBy);
            Pageable pageRequest = PageRequest.of(page, PRODUCTS_PER_PAGE, sort);

            Product product = Product.builder().build();
            List<Purchase> purchaseList = new ArrayList<>();
            Purchase purchase = Purchase.builder()
                .member(member)
                .product(product)
                .build();
            purchaseList.add(purchase);

            Page<Purchase> purchases = new PageImpl<>(purchaseList, pageRequest,
                purchaseList.size());

            given(purchaseRepository.findAllByMember(member, pageRequest)).willReturn(purchases);
            given(productRepository.findById(any())).willReturn(Optional.empty());

            // when
            Exception exception = assertThrows(CustomException.class,
                () -> purchaseService.getPurchases(member, page, sortBy, order));

            // then
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }

    }
}