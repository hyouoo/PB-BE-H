package com.example.purebasketbe.domain.admin.entity;

import com.example.purebasketbe.domain.member.entity.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "admin")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private final UserRole role = UserRole.ADMIN;
}
