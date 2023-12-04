package com.example.purebasketbe.domain.product.entity;

import lombok.Getter;

@Getter
public enum Event {
    NORMAL(1.00),
    DISCOUNT(0.50);  // 해결 필요

    private double rate;

    Event(double rate) {
        this.rate = rate;
    }
}
