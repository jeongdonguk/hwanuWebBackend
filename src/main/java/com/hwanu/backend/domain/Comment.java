package com.hwanu.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "comment")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId; // 댓글 ID

//    @Column(nullable = false)
//    private Long boardId; // 게시글 ID (Post와의 연관은 이후 설정 가능)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boardId")
    private Board board;

    @Column
    private Long parentId; // 대댓글일 경우 부모 댓글 ID (null 가능)

    @Column(nullable = false)
    private Long memberId; // 작성자 회원 ID

    @Column(nullable = false, length = 100)
    private String email; // 작성자 이메일

    @Column(length = 30)
    private String nickname; // 작성자 닉네임

    @Column(nullable = false, length = 1000)
    private String content; // 댓글 내용

    @Column(nullable = false)
    private int likeCnt = 0; // 좋아요 수


}
