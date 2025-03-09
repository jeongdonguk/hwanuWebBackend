package com.hwanu.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Member extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 100)
    private String password;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(length = 300)
    private String profileImage;

    @Column(nullable = false, length = 100)
    private String role;

    @Column(nullable = false)
    private String provider;

    @Column(length = 50)
    private String providerId;

    @Column(nullable = false)
    private String status;

    private LocalDateTime lastLogin;
}
