package com.example.purebasketbe.domain.purchase;

import com.example.purebasketbe.domain.purchase.entity.PurchaseDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseDetailRepository extends JpaRepository<PurchaseDetail, Long> {

}
