package com.example.purebasketbe.domain.purchase;

import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.purchase.entity.Purchase;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT p FROM Purchase p JOIN FETCH p.product WHERE p.member = :member")
    Page<Purchase> findAllByMember(Member member, Pageable pageable);

}