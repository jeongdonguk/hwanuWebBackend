package com.hwanu.backend.service;

import com.hwanu.backend.DTO.BoardResponseDTO;
import com.hwanu.backend.domain.Board;
import com.hwanu.backend.repository.BoardRepository;
import com.hwanu.backend.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Log4j2
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @Override
    public Page<BoardResponseDTO> getAllBoards(Pageable pageable) {

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "boardId")
        );

        return boardRepository.findAll(sortedPageable)
                .map(board -> {
                    Long commentCount = commentRepository.countByBoard(board);
                    return BoardResponseDTO.fromEntity(board, commentCount);
                });
    }
}
