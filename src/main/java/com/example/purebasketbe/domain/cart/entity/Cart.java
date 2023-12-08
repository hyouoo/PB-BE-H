package com.example.purebasketbe.domain.cart.entity;

import com.example.purebasketbe.domain.cart.dto.CartRequestDto;
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
    @JoinColumn(name = "member_id")
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

    public static Cart of(Product product, Member member, CartRequestDto requestDto) {
        int amount = requestDto == null ? 1 : requestDto.amount();
        return Cart.builder()
                .amount(amount)
                .member(member)
                .product(product)
                .build();
    }

    public void changeAmount(CartRequestDto requestDto) {
        this.amount = requestDto.amount();
    }
}