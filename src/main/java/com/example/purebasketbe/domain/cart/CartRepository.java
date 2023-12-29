package com.example.purebasketbe.domain.cart;

import com.example.purebasketbe.domain.cart.entity.Cart;
import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("select exists (select c.id from Cart c where c.product = :product)")
    boolean existsProduct(Product product);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.member=:member AND c.product IN (:products)")
    void deleteByMemberAndProductIn(Member member, List<Product> products);

    @Query("SELECT c FROM Cart c JOIN FETCH c.product WHERE c.member = :member")
    List<Cart> findAllByMember(Member member);

    void deleteAllByMemberAndProductIn(Member member, List<Product> productList);

    Optional<Cart> findByProductIdAndMember(Long productId, Member member);

}