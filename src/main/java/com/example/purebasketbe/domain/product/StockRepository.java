package com.example.purebasketbe.domain.product;

import com.example.purebasketbe.domain.product.entity.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s WHERE s.product.id IN :requestedProductsIds")
    List<Stock> findAllByProductIdIn(List<Long> requestedProductsIds);

    Stock findByProductId(Long productId);
}
