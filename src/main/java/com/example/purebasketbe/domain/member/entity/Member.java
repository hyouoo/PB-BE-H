package com.example.purebasketbe.domain.member.entity;

import com.example.purebasketbe.domain.member.dto.SignupRequestDto;
import jakarta.persistence.*;
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
    @Enumerated(value = EnumType.STRING)
    private final UserRole role = UserRole.MEMBER;

    @Column(nullable = false)
    private boolean deleted = false;

    @Builder
    private Member(String email, String password, String phone, String address, UserRole role, boolean deleted){
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.deleted = deleted;
    }

    public static Member of(SignupRequestDto requestDto, String password){
        return Member.builder()
            .email(requestDto.email())
            .password(password)
            .phone(requestDto.phone())
            .address(requestDto.address())
            .role(UserRole.MEMBER)
            .deleted(requestDto.deleted())
            .build();
    }
}
