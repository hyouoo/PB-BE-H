package com.example.purebasketbe.domain.purchase;

import com.example.purebasketbe.domain.cart.CartRepository;
import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.product.ProductRepository;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.purchase.dto.PurchaseResponseDto;
import com.example.purebasketbe.domain.purchase.dto.PurchaseRequestDto.PurchaseDetail;
import com.example.purebasketbe.domain.purchase.entity.Purchase;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    private final int PRODUCTS_PER_PAGE = 10;

    @Transactional
    public void purchaseProducts(final List<PurchaseDetail> purchaseRequestDto, Member member) {
        int size = purchaseRequestDto.size();
        List<PurchaseDetail> sortedPurchaseDetailList = purchaseRequestDto.stream()
                .sorted(Comparator.comparing(PurchaseDetail::getProductId)).toList();
        List<Long> requestIds = sortedPurchaseDetailList.stream()
                .map(PurchaseDetail::getProductId).toList();

        List<Product> validProductList = productRepository.findByIdIn(requestIds);
        validateProducts(size, validProductList);
        List<Integer> amountList = sortedPurchaseDetailList.stream()
                .map(PurchaseDetail::getAmount).toList();

        List<Purchase> purchaseList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Product product = validProductList.get(i);
            int amount = amountList.get(i);
            checkProductStock(product, amount);

            Purchase purchase = Purchase.of(product, amount, member);
            purchaseList.add(purchase);

            product.incrementSalesCount(amount);
            product.decrementStock(amount);
        }

        purchaseRepository.saveAll(purchaseList);
        cartRepository.deleteByUserAndProductIn(member, validProductList);
    }

    @Transactional(readOnly = true)
    public Page<PurchaseResponseDto> getPurchases(Member member, int page, String sortBy, String order) {
        Sort.Direction direction = Direction.valueOf(order.toUpperCase());
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, PRODUCTS_PER_PAGE, sort);

        Page<Purchase> purchases = purchaseRepository.findAllByMember(member, pageable);

        return purchases.map(purchase -> {
            Product product = getProductById(purchase.getProduct().getId());
            return PurchaseResponseDto.of(product, purchase);
        });
    }

    private static void validateProducts(int size, List<Product> validProductList) {
        if (size != validProductList.size()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    private static void checkProductStock(Product product, int amount) {
        if (product.getStock() < amount) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_PRODUCT);
        }
    }

    private Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
        );
    }
}