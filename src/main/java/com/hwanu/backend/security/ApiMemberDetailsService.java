package com.hwanu.backend.security;


import com.hwanu.backend.domain.Member;
import com.hwanu.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 *  Spring Security에서 사용자 인증을 담당하는 서비스
 * - 데이터베이스에서 `email`을 기반으로 회원 정보를 조회하여 `UserDetails` 객체로 변환
 * - 로그인 시 SecurityContext에 저장되는 인증 정보 생성
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class ApiMemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;  //  DB에서 회원 정보 조회

    /**
     *  `UserDetailsService`의 `loadUserByUsername` 메서드 구현
     * @param email 사용자의 이메일 (로그인 ID)
     * @return `UserDetails` 객체 (Spring Security 인증용)
     * @throws UsernameNotFoundException 사용자가 없을 경우 예외 발생
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.info(" CustomUserDetailsService: 사용자 이메일 조회 → {}", email);

        //  이메일로 회원 정보 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        log.info(" 사용자 조회 성공: {}", member);

        //  Spring Security가 이해할 수 있는 `UserDetails` 객체로 변환
        return org.springframework.security.core.userdetails.User.builder()
                .username(member.getEmail())  // Spring Security에서 username으로 사용할 값 (이메일)
                .password(member.getPassword())  // Spring Security에서 사용할 패스워드
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole())))  // 역할(Role) 설정
                .build();
    }
}