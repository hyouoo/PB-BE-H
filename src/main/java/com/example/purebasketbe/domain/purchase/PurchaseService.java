package com.example.purebasketbe.domain.purchase;

import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.product.ProductRepository;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.purchase.dto.PurchaseRequestDto.PurchaseDetail;
import com.example.purebasketbe.domain.purchase.dto.PurchaseResponseDto;
import com.example.purebasketbe.domain.purchase.entity.Purchase;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
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
    private final ProductRepository productRepository;

    private final int PRODUCTS_PER_PAGE = 10;

    @Transactional
    public void processPurchase(List<PurchaseDetail> purchaseRequestDto, List<Long> requestedProductsIds, int size) {
        List<Product> validProductList = productRepository.findByIdInAndDeleted(requestedProductsIds, false);
        List<Integer> amountList = purchaseRequestDto.stream().map(PurchaseDetail::amount).toList();

        validateProducts(size, validProductList);
        for (int i = 0; i < size; i++) {
            Product product = validProductList.get(i);
            int amount = amountList.get(i);
            checkProductStock(product, amount);
            product.decrementStock(amount);
            product.incrementSalesCount(amount);
        }
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