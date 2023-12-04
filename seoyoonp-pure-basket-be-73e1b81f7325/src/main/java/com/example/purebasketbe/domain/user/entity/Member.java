package com.example.purebasketbe.domain.user.entity;

import com.example.purebasketbe.domain.user.dto.SignupRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private boolean deleted = false;

    @Builder
    private Member(String email, String password, String phone, String address, boolean deleted){
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.deleted = deleted;
    }

    public static Member of(SignupRequestDto requestDto, String password){
        return Member.builder()
            .email(requestDto.getEmail())
            .password(password)
            .phone(requestDto.getPhone())
            .address(requestDto.getAddress())
            .deleted(requestDto.isDeleted())
            .build();
    }
}
