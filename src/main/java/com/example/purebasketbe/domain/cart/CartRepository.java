package com.example.purebasketbe.domain.cart;

import com.example.purebasketbe.domain.cart.entity.Cart;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository <Cart,Long> {
    Optional<Cart> findByProductId(Long productId);
    Page<Cart> findAll(Pageable pageable);
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.member=:member AND c.product IN (:products)")
    void deleteByUserAndProductIn(Member member, List<Product> products);

    @Query("SELECT p FROM Cart p JOIN FETCH p.product WHERE p.member = :member")
    List<Cart> findAllByMember(Member member);


    void deleteAllByMemberAndProductIn(Member member, List<Product> productList);
}