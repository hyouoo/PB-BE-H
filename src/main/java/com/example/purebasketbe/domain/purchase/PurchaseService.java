package com.example.purebasketbe.domain.purchase;

import com.example.purebasketbe.domain.cart.CartRepository;
import com.example.purebasketbe.domain.product.ProductRepository;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.purchase.dto.PurchaseHistoryDto;
import com.example.purebasketbe.domain.purchase.dto.PurchaseRequestDto.PurchaseDetail;
import com.example.purebasketbe.domain.purchase.entity.Purchase;
import com.example.purebasketbe.domain.user.entity.User;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;


    @Transactional
    public void purchaseProducts(final List<PurchaseDetail> purchaseRequestDtoList, User user) {
        // TODO: List size 1인 경우와 아닌 경우 나누기
        List<PurchaseDetail> sortedPurchaseDetailList = purchaseRequestDtoList.stream()
            .sorted(Comparator.comparing(PurchaseDetail::getProductId)).toList();
        List<Long> requestIds = sortedPurchaseDetailList.stream().map(PurchaseDetail::getProductId).toList();
        List<Product> validProductList = productRepository.findByIdIn(requestIds);
        if (validProductList.isEmpty()) {
            return;
        }
        List<Long> validProductIdList = validProductList.stream().map(Product::getId).toList();
        List<Integer> amountList = sortedPurchaseDetailList.stream()
            .filter(purchaseDetail -> validProductIdList.contains(purchaseDetail.getProductId()))
            .map(PurchaseDetail::getAmount).toList();

        List<Purchase> purchaseList = new ArrayList<>();

        for (int i = 0; i < validProductList.size(); i++) {
            Product product = validProductList.get(i);
            int amount = amountList.get(i);
            checkProductStock(product, amount);

            Purchase purchase = Purchase.of(product, amount, user);
            purchaseList.add(purchase);

            product.incrementCount(amount);
            product.decrementStock(amount);
        }

        purchaseRepository.saveAll(purchaseList);
        cartRepository.deleteByUserAndProductIn(user, validProductList);
    }



    @Transactional(readOnly = true)
    public List<PurchaseHistoryDto> getPurchaseHistory(User user) {
        List<Purchase> purchaseList = purchaseRepository.findAllByUser(user);

        return purchaseList.stream().map(purchase -> {
            Product product = getProductById(purchase.getProduct().getId());
            return PurchaseHistoryDto.of(product, purchase);
        }).toList();
    }

    private static void checkProductStock(Product product, int amount) {
        if (product.getQuantity() < amount) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_PRODUCT);
        }
    }

    private Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(
            () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
        );
    }
}
