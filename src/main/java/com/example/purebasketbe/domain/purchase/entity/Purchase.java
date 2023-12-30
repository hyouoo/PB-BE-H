package com.example.purebasketbe.domain.purchase.entity;

import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.product.entity.Product;
import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private int totalPrice;

    @Builder
    private Purchase(Member member, int totalPrice) {

        this.member = member;
        this.totalPrice = totalPrice;
    }

    public static Purchase of(Member member, int totalPrice) {
        return Purchase.builder()
                .member(member)
                .totalPrice(totalPrice)
                .build();
    }
}