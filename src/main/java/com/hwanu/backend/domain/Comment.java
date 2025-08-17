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
    @Column(name = "comment_id")
    private Long commentId; // 댓글 ID

//    @Column(nullable = false)
//    private Long boardId; // 게시글 ID (Post와의 연관은 이후 설정 가능)

    // Board와의 연관성 설정
    // @ManyToOne : 댓글 N개가 하나의 게시글에 속함(다대일 관계)
    //   ===> 없어도 되긴하는데 cascade나 이런류의 기능을 위해, 관계표시용으로도 좋고
    // fetch = FetchType.LAZY => Board정보는 실제로 필요할때만 조회(지연로딩)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(name = "board_id", insertable = false ,updatable = false)
    private Long boardIdRef;

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
