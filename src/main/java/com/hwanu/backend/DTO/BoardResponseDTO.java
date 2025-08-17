package com.hwanu.backend.DTO;

import com.hwanu.backend.domain.Board;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponseDTO {
    private Long boardId;
    private String title;
    private Long comment_cnt;
    private String email;
    private String nickname;
    private int viewCnt;
    private int likeCnt;

    // 댓글 수량 입력 및 필요 데이터 오브젝트화
    public static BoardResponseDTO fromEntity(Board board, Long commentCnt) {
        BoardResponseDTO dto = new BoardResponseDTO();
        dto.setBoardId(board.getBoardId());
        dto.setTitle(board.getTitle());
        dto.setComment_cnt(commentCnt);
        dto.setEmail(board.getEmail());
        dto.setNickname(board.getNickname());
        dto.setViewCnt(board.getViewCnt());
        dto.setLikeCnt(board.getLikeCnt());
        return dto;
    }

}
