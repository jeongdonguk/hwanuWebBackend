package com.hwanu.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity // 엔티티임을 명시 == DB의 row한줄
@Getter
@Builder
@NoArgsConstructor // 기본생성자 자동
@AllArgsConstructor // 모든 필드를 가진 생성자 자동
@ToString
@Table(name = "board") // 맵핑되는 테이블명

// 생성일, 수정일이 자동으로 관리되는 추상클래스 상속
public class Board extends BaseEntity{

    @Id // 기본키임을 나타냄
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB의 오토인크리먼트 사용하겠단 뜻
    private Long boardId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String email;

    private String nickname;

    @Column(nullable = false, length = 300) // 최대 300자 제한
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private int viewCnt = 0;

    @Column(nullable = false)
    @Builder.Default
    private int likeCnt = 0;

    @Builder.Default
    private String notiYN = "N";


}
