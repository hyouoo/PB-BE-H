package com.example.purebasketbe.domain.purchase;

import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.product.ProductRepository;
import com.example.purebasketbe.domain.product.StockRepository;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.product.entity.Stock;
import com.example.purebasketbe.domain.purchase.dto.PurchaseRequestDto.PurchaseDetail;
import com.example.purebasketbe.domain.purchase.dto.PurchaseResponseDto;
import com.example.purebasketbe.domain.purchase.entity.Purchase;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import com.example.purebasketbe.global.kafka.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final KafkaService kafkaService;

    private final int PRODUCTS_PER_PAGE = 10;

    @Transactional
    public void purchaseProducts(List<PurchaseDetail> purchaseRequestDto, Member member) {
        int size = purchaseRequestDto.size();
        // Lock 적용
        List<Long> requestedProductsIds = purchaseRequestDto.stream()
                .map(PurchaseDetail::productId).toList();
//        List<Stock> stockList = stockRepository.findAllByProductIdIn(requestedProductsIds);

        List<Product> validProductList = productRepository.findByIdInAndDeleted(requestedProductsIds, false);
        validateProducts(size, validProductList);

//        List<Integer> amountList = purchaseRequestDto.stream().map(PurchaseDetail::amount).toList();
//        for (int i = 0; i < size; i++) {
//            Stock stock = stockList.get(i);
//            int amount = amountList.get(i);
//            checkProductStock(stock, amount);
//            stock.decrementStock(amount);
//        }

        kafkaService.sendPurchaseToKafka(purchaseRequestDto, member);
        log.info("회원 {}: 상품 구매 요청 적재", member.getId());
    }

    @Transactional(readOnly = true)
    public Page<PurchaseResponseDto> getPurchases(Member member, int page, String sortBy, String order) {
        Sort.Direction direction = Direction.valueOf(order.toUpperCase());
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, PRODUCTS_PER_PAGE, sort);

        Page<Purchase> purchases = purchaseRepository.findAllByMember(member, pageable);

        return purchases.map(PurchaseResponseDto::from);
    }

    private static void validateProducts(int size, List<Product> validProductList) {
        if (size != validProductList.size()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }
    }

//    private static void checkProductStock(Stock stock, int amount) {
//        if (stock.getStock() < amount) {
//            throw new CustomException(ErrorCode.NOT_ENOUGH_PRODUCT);
//        }
//    }
}