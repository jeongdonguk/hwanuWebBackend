package com.hwanu.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity // 이 데이터가 테이블의 row임을 암시
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Member extends BaseEntity{ // 최초/수정 날짜 자동 업데이트

    @Id // 기본키
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 오토인크리먼트 사용
    private long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 100)
    private String password;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(length = 300)
    private String profileImage;

    @Column(length = 15)
    @Builder.Default
    private String role = "USER"; // 기본값 지정

    @Builder.Default
    private String provider = "LOCAL";

    @Column(length = 50)
    private String providerId;

    @Builder.Default
    private String status = "ACTIVATE";

    private LocalDateTime lastLogin;

    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
}
