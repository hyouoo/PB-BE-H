package com.example.purebasketbe.domain.cart;

import com.example.purebasketbe.domain.cart.entity.Cart;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository <Cart,Long> {

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.user=:user AND c.product IN (:products)")
    void deleteByUserAndProductIn(User user, List<Product> products);
}
