package com.hwanu.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Setter // setter 허용 ( 토큰 갱신과 같은 변경이 필요한 경우)
    @Column(nullable = false, name = "refresh_token")
    private String refreshToken;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Setter // setter 허용 ( 토큰 발급, 만료 연장시 사용)
    @Column(nullable = false, name = "expires_at")
    private LocalDateTime expiresAt;


}
