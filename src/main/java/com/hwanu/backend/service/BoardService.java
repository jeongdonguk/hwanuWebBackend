package com.hwanu.backend.service;

import com.hwanu.backend.DTO.*;
import com.hwanu.backend.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardService {

    Page<BoardResponseDTO> getAllBoards(Pageable pageable);

    PostReadResponseDTO getBoardById(Long boardId);

    List<CommentResponseDTO> getCommentByBoardId(Long boardId);

    Long insertBoard(PostWriteDTO postWriteDTO);

    Long insertComment(CommentWriteDTO commentWriteDTO);
}
