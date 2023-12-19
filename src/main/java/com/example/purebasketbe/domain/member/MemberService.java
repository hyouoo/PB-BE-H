package com.example.purebasketbe.domain.member;

import com.example.purebasketbe.domain.member.dto.SignupRequestDto;
import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerMember(SignupRequestDto requestDto) {
        String email = requestDto.email();
        String password = passwordEncoder.encode(requestDto.password());

        checkIfEmailExist(email);
        Member member = Member.of(requestDto, password);
        memberRepository.save(member);
    }

    @KafkaListener(topics = "event", groupId = "${spring.kafka.consumer.group-id}")
    public void printMessage(String msg) {
        log.info("Message from Kafka : {}", msg);
    }

    private void checkIfEmailExist(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));
    }
}