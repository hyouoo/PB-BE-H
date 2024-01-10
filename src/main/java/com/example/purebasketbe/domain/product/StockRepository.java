package com.example.purebasketbe.domain.product;

import com.example.purebasketbe.domain.product.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
  
    @Modifying
    @Query("update Stock s set s.stock = s.stock - :amount where s.stock > :amount AND s.product.id = :productId")
    void updateStockByAmountByProductId(int amount, long productId);

    Stock findByProductId(Long productId);
}
