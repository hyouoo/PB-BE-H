package com.example.purebasketbe.domain.purchase.entity;

import com.example.purebasketbe.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy="purchase", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<PurchaseDetail> purchaseDetails = new ArrayList<>();

    @Column(nullable = false)
    private int totalPrice;

    @Builder
    private Purchase(Member member, int totalPrice) {

        this.member = member;
        this.totalPrice = totalPrice;
    }
}