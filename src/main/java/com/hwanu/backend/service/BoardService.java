package com.hwanu.backend.service;

import com.hwanu.backend.DTO.BoardResponseDTO;
import com.hwanu.backend.DTO.PostReadResponseDTO;
import com.hwanu.backend.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardService {

    public Page<BoardResponseDTO> getAllBoards(Pageable pageable);

    public PostReadResponseDTO getBoardById(Long boardId);
}
