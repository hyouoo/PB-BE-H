package com.example.purebasketbe.domain.purchase;

import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.product.ProductRepository;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.purchase.dto.PurchaseRequestDto.PurchaseDetail;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import com.example.purebasketbe.global.kafka.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseFacade {

    private final PurchaseService purchaseService;
    private final KafkaService kafkaService;
    private final ProductRepository productRepository;

    public void purchaseProducts(List<PurchaseDetail> purchaseRequestDto, Member member) {
        int size = purchaseRequestDto.size();

        List<Long> requestedProductsIds = purchaseRequestDto.stream()
                .map(PurchaseDetail::productId).toList();
        List<Product> validProductList = productRepository.findByIdInAndDeleted(requestedProductsIds, false);
        validateProducts(size, validProductList);

        purchaseService.processPurchase(purchaseRequestDto, size, requestedProductsIds);
        kafkaService.sendPurchaseToKafka(purchaseRequestDto, member);
        log.info("회원 {}: 상품 구매 요청 적재", member.getId());
    }

    private static void validateProducts(int size, List<Product> validProductList) {
        if (size != validProductList.size()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }
    }

}
