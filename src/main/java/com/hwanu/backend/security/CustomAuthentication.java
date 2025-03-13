package com.hwanu.backend.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class CustomAuthentication extends AbstractAuthenticationToken {

    private String email;

    public CustomAuthentication(String email) {
        super(Collections.emptyList()); // 권한정보 없음
        this.email = email;
        setAuthenticated(true); // 인증 완료 상태로 설정
    }

    @Override
    public Object getCredentials() {
        return null; // jwt 인증으로 별도 비번 필요없음
    }

    @Override
    public Object getPrincipal() {
        return email; // 사용자의 이메일을 주식별자로 설정
    }
}
