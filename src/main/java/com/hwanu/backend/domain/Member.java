package com.hwanu.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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

    @Column(length = 15)
    @Builder.Default
    private String role = "USER";

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
