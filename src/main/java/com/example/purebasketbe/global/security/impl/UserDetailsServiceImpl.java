package com.example.purebasketbe.global.security.impl;

import com.example.purebasketbe.domain.admin.AdminRepository;
import com.example.purebasketbe.domain.admin.entity.Admin;
import com.example.purebasketbe.domain.member.MemberRepository;
import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 먼저 관리자 Repository에서 이메일로 관리자 정보 조회 시도
        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            // 관리자 정보가 존재하는 경우 UserDetailsImpl 객체 생성하여 반환
            return new UserDetailsImpl(admin.get());
        } else {
            // 관리자 정보가 없다면, 일반 사용자 Repository에서 조회
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVALID_EMAIL_PASSWORD));
            // 일반 사용자 정보로 UserDetailsImpl 객체 생성하여 반환
            return new UserDetailsImpl(member);
        }
    }
}