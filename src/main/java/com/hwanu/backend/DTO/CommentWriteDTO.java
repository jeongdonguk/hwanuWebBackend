package com.hwanu.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentWriteDTO {

    private Long commentId; // 댓글 ID
    private Long boardId;
    private Long parentId; // 대댓글일 경우 부모 댓글 ID (null 가능)
    private Long memberId; // 작성자 회원 ID
    private String email; // 작성자 이메일
    private String nickname; // 작성자 닉네임
    private String content; // 댓글 내용


}
