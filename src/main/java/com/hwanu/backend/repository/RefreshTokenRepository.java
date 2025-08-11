package com.hwanu.backend.repository;

import com.hwanu.backend.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByEmail(String email);

    // 로그아웃시
    void deleteByEmail(String email);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
