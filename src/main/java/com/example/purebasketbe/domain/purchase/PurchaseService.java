package com.example.purebasketbe.domain.purchase;

import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.product.StockRepository;
import com.example.purebasketbe.domain.product.entity.Stock;
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
    private final StockRepository stockRepository;

    private final int PRODUCTS_PER_PAGE = 10;

    @Transactional
    public void processPurchase(List<PurchaseDetail> purchaseRequestDto, int size, List<Long> requestedProductsIds) {

        List<Stock> stockList = stockRepository.findAllByProductIdIn(requestedProductsIds);
        List<Integer> amountList = purchaseRequestDto.stream().map(PurchaseDetail::amount).toList();

        for (int i = 0; i < size; i++) {
            Stock stock = stockList.get(i);
            int amount = amountList.get(i);
            checkProductStock(stock, amount);
            stock.decrementStock(amount);
        }

    }


    @Transactional(readOnly = true)
    public Page<PurchaseResponseDto> getPurchases(Member member, int page, String sortBy, String order) {
        Sort.Direction direction = Direction.valueOf(order.toUpperCase());
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, PRODUCTS_PER_PAGE, sort);

        Page<Purchase> purchases = purchaseRepository.findAllByMember(member, pageable);

        return purchases.map(PurchaseResponseDto::from);
    }


    private static void checkProductStock(Stock stock, int amount) {
        if (stock.getStock() < amount) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_PRODUCT);
        }
    }

}