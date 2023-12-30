package com.example.purebasketbe.domain.product;

import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.product.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    @Query("SELECT s FROM Stock s WHERE s.product IN (:productList)")
    List<Stock> findAllByProductIn(List<Product> productList);
}
