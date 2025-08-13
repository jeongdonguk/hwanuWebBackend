package com.hwanu.backend.service;

import com.hwanu.backend.DTO.BoardResponseDTO;
import com.hwanu.backend.DTO.PostReadResponseDTO;
import com.hwanu.backend.domain.Board;
import com.hwanu.backend.repository.BoardRepository;
import com.hwanu.backend.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

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

    @Override
    public PostReadResponseDTO getBoardById(Long boardId) {
        Optional<Board> board = boardRepository.findById(boardId);

        PostReadResponseDTO postReadResponseDTO = modelMapper.map(board, PostReadResponseDTO.class);

        return postReadResponseDTO;

    }
}
