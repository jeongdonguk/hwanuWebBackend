package com.hwanu.backend.service;

import com.hwanu.backend.DTO.MemberLoginDTO;
import com.hwanu.backend.DTO.MemberRegisterDTO;
import com.hwanu.backend.domain.Member;
import com.hwanu.backend.domain.RefreshToken;
import com.hwanu.backend.repository.RefreshTokenRepository;
import com.hwanu.backend.repository.MemberRepository;
import com.hwanu.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    @Override
    public String register(MemberRegisterDTO memberRegisterDTO) {
        if(memberRepository.findByEmail(memberRegisterDTO.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Member member = Member.builder()
                .email(memberRegisterDTO.getEmail())
                .password(passwordEncoder.encode(memberRegisterDTO.getPassword()))
                .nickname(memberRegisterDTO.getNickname())
                .build();
        memberRepository.save(member);

        return "회원가입 성공";
    }

    @Override
    public String login(MemberLoginDTO memberLoginDTO) {
        Member member = memberRepository.findByEmail(memberLoginDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다."));

        if (!passwordEncoder.matches(memberLoginDTO.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다.");
        }

        member.updateLastLogin();
        memberRepository.save(member);

        String accessToken = jwtUtil.generateToken(member.getEmail(), member.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(member.getEmail());

        tokenService.saveRefreshToken(member.getEmail(), refreshToken);

        return accessToken;
    }
}
