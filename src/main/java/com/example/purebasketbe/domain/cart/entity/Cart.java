package com.example.purebasketbe.domain.cart.entity;

import com.example.purebasketbe.domain.cart.dto.CartRequestDto;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.member.entity.Member;
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
@Table(name = "cart")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1)
    @Column(nullable = false)
    private int amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    private Cart(int amount, Member member, Product product) {
        this.amount = amount;
        this.member = member;
        this.product = product;
    }

    public static Cart of(CartRequestDto requestDto, Member member, Product product) {
        return Cart.builder()
            .amount(requestDto.getAmount())
            .user(member)
            .product(product)
            .build();
    }
}
