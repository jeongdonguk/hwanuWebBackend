package com.hwanu.backend.service;

import com.hwanu.backend.DTO.MemberDTO;
import com.hwanu.backend.DTO.MemberLoginDTO;
import com.hwanu.backend.DTO.MemberRegisterDTO;
import com.hwanu.backend.DTO.TokenResponseDTO;
import com.hwanu.backend.domain.Member;
import com.hwanu.backend.repository.MemberRepository;
import com.hwanu.backend.security.issuer.JwtIssuer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtIssuer jwtUtil;

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
    public TokenResponseDTO login(MemberLoginDTO memberLoginDTO) {
        Member member = memberRepository.findByEmail(memberLoginDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다."));

        if (!passwordEncoder.matches(memberLoginDTO.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다.");
        }

        member.updateLastLogin();
        memberRepository.save(member);

        String accessToken = jwtUtil.generateAccessToken(member);
        String refreshToken = jwtUtil.generateRefreshToken(member.getEmail());

        TokenResponseDTO tokenResponseDTO = TokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        tokenService.saveRefreshToken(member.getEmail(), refreshToken);

        return tokenResponseDTO;
    }

}
