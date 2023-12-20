package com.example.purebasketbe.domain.product;

import com.example.purebasketbe.domain.product.entity.Event;
import com.example.purebasketbe.domain.product.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);

    Page<Product> findAllByDeletedAndEvent(boolean isDeleted, Event event, Pageable pageable);

    Page<Product> findAllByDeletedAndEventAndCategoryAndNameContains(boolean isDeleted, Event event,
                                                                     String category, String query, Pageable pageable);

    Page<Product> findAllByDeletedAndEventAndNameContains(boolean isDeleted, Event event, String query, Pageable pageable);

    Optional<Product> findByIdAndDeleted(Long productId, boolean isDeleted);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id in (:requestIds) and p.deleted=:isDeleted")
    List<Product> findByIdInAndDeleted(List<Long> requestIds, boolean isDeleted);
}
