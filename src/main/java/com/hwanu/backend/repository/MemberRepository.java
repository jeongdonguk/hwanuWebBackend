package com.hwanu.backend.repository;

import com.hwanu.backend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query(value = "select now()" , nativeQuery = true)
    String getTime();

    Optional<Member> findByEmail(String email);

    Optional<Member> findById(Long id);

}
