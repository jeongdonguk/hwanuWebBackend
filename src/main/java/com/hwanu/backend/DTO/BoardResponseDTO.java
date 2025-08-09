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

    // 엔티티 모든 필드가 아닌 필요한 데이트들만 추려서 이동하기 위해 사용
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
