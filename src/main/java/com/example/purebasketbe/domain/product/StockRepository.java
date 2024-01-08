package com.example.purebasketbe.domain.product;

import com.example.purebasketbe.domain.product.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findByProductId(Long productId);
}
