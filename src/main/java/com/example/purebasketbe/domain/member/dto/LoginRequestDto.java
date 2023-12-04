package com.example.purebasketbe.domain.member.dto;

public record LoginRequestDto (
    String email,
    String password
) { }
