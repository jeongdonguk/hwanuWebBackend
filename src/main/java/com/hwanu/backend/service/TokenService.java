package com.hwanu.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Transactional
public interface TokenService {
    // refresh 토큰 db에 저장
    public void saveRefreshToken(String email, String refreshToken);

    // access 토큰 재발급
    public Map<String, String> refreshAccessToken(String refreshToken);

    // refresh 토큰 삭제 - 로그아웃 시
    public void deleteRefreshToken(String email);

//    public String validateAndGetEmail(String token);
}
