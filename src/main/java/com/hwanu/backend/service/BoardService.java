package com.hwanu.backend.service;

import com.hwanu.backend.DTO.BoardResponseDTO;
import com.hwanu.backend.DTO.CommentResponseDTO;
import com.hwanu.backend.DTO.PostReadResponseDTO;
import com.hwanu.backend.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardService {

    Page<BoardResponseDTO> getAllBoards(Pageable pageable);

    PostReadResponseDTO getBoardById(Long boardId);

    List<CommentResponseDTO> getCommentByBoardId(Long boardId);
}
