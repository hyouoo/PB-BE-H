package com.example.purebasketbe.domain.purchase.entity;

import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.Member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "purchase")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Purchase extends TimeStamp{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1)
    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    private Purchase(int amount, int price, Member member, Product product) {
        this.amount = amount;
        this.price = price;
        this.member = member;
        this.product = product;
    }

//    public static Purchase from(PurchaseRequestDto requestDto) {
//        return Purchase.builder()
//            .amount(requestDto.)
//    }
}
