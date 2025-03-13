package com.hwanu.backend.repository;

import com.hwanu.backend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.security.Provider;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query(value = "select now()" , nativeQuery = true)
    String getTime();

    Optional<Member> findByEmail(String email);

    Optional<Member> findById(long id);

    // 소셜 로그인 회원 정보
//    Optional<Member> findByProviderAndProviderId(Provider provider, Provider providerId);

}
