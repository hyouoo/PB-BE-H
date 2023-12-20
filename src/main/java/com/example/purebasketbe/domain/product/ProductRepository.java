package com.example.purebasketbe.domain.product;

import com.example.purebasketbe.domain.product.entity.Event;
import com.example.purebasketbe.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);

    Page<Product> findAllByDeletedAndEvent(boolean isDeleted, Event event, Pageable pageable);

    Page<Product> findAllByDeletedAndEventAndCategoryAndNameContains(boolean isDeleted, Event event,
                                                                     String category, String query, Pageable pageable);

    Page<Product> findAllByDeletedAndEventAndNameContains(boolean isDeleted, Event event, String query, Pageable pageable);

    Optional<Product> findByIdAndDeleted(Long productId, boolean isDeleted);

    List<Product> findByIdInAndDeleted(List<Long> requestIds, boolean isDeleted);
}
