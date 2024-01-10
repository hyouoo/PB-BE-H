package com.example.purebasketbe.domain.purchase.entity;

import com.example.purebasketbe.domain.product.entity.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "purchase_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    @Min(value = 1)
    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private int price;
}
