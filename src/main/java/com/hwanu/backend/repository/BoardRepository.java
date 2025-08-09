package com.hwanu.backend.repository;

import com.hwanu.backend.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;


// 리포지토리 인터페이스 (Board 엔티티 전용)
// DB board와 연동하여 CRUD 등의 작업 진행
public interface BoardRepository extends JpaRepository<Board, Long> {
    // JpaRepository<엔티티명, PK타입> 을 상송받아
    // 
}
