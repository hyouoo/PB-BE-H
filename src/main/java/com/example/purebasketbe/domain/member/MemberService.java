package com.example.purebasketbe.domain.member;

import com.example.purebasketbe.domain.member.dto.SignupRequestDto;
import com.example.purebasketbe.domain.member.entity.Member;
import com.example.purebasketbe.domain.product.dto.ProductResponseDto;
import com.example.purebasketbe.global.exception.CustomException;
import com.example.purebasketbe.global.exception.ErrorCode;
import com.example.purebasketbe.global.tool.EmailContents;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.purebasketbe.global.kafka.KafkaService.TOPIC_EVENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    @Transactional
    public void registerMember(SignupRequestDto requestDto) {
        String email = requestDto.email();
        String password = passwordEncoder.encode(requestDto.password());

        checkIfEmailExist(email);
        Member member = Member.of(requestDto, password);
        memberRepository.save(member);
    }

    @KafkaListener(topics = TOPIC_EVENT, groupId = "${spring.kafka.consumer.group-id}")
    public void sendEmailToMembers(ProductResponseDto responseDto) {
        log.info("method called : sendEmailToMembers");
        List<String> emailList = memberRepository.findAllEmails();
        EmailContents contents = EmailContents.from(responseDto);
        String subject = contents.subject();
        String text = contents.text();

        for (String email : emailList) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(subject);
            message.setText(text);
            javaMailSender.send(message);
        }
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