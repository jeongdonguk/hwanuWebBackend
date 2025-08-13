package com.hwanu.backend.service;

import com.hwanu.backend.domain.Member;
import com.hwanu.backend.domain.RefreshToken;
import com.hwanu.backend.repository.MemberRepository;
import com.hwanu.backend.repository.RefreshTokenRepository;
import com.hwanu.backend.security.issuer.JwtIssuer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final JwtIssuer jwtUtil;

    // 로그인시 호출
    @Override
    public void saveRefreshToken(String email, String refreshToken) {
        // 이메일로 Refresh 토큰을 조회
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByEmail(email);

        // 토큰이 있다면
        if (existingToken.isPresent()) {
            RefreshToken dbRefreshToken = existingToken.get();
            dbRefreshToken.setRefreshToken(refreshToken);
            dbRefreshToken.setExpiresAt(LocalDateTime.now().plusDays(14));
            refreshTokenRepository.save(dbRefreshToken);
        // 토큰이 없을 경우 만들어서 입력
        } else {
            RefreshToken newRefreshToken = RefreshToken.builder()
                    .email(email)
                    .refreshToken(refreshToken)
                    .expiresAt(LocalDateTime.now().plusDays(14))
                    .createdAt(LocalDateTime.now())
                    .build();
            refreshTokenRepository.save(newRefreshToken);
        }
    }

    @Override
    public Map<String, String> refreshAccessToken(String refreshToken) {
        // 받은 토큰에서 email 정보를 추출하여 db에서 가져옴
        Optional<RefreshToken> dbRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken);
        Map<String, String> map = new HashMap<>();

        if (dbRefreshToken.isPresent()) {
            String email = dbRefreshToken.get().getEmail();
            String role = memberRepository.findByEmail(email).map(Member::getRole).orElse("USER");
            String newAccessToken = jwtUtil.generateAccessToken(email, role);
            map.put("hwanuAccessToken", newAccessToken);
            map.put("email", email);
        } else {
            map.put("error","유효하지 않은 Refresh Token입니다.");
        }

        return map;

    }

//    @Override
//    public String validateAndGetEmail(String token){
//        Optional<RefreshToken> dbRefreshToken = refreshTokenRepository.findByRefreshToken(token);
//
//        if (dbRefreshToken.isPresent()) {}
//    }

    // 로그아웃 시 호츌
    @Override
    public void deleteRefreshToken(String email) {
        refreshTokenRepository.deleteByEmail(email);
    }
}
