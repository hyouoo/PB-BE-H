package com.example.purebasketbe.domain.Member.dto;

public record LoginRequestDto (
    String email,
    String password
) { }
