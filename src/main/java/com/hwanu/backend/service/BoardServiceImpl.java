package com.hwanu.backend.service;

import com.hwanu.backend.DTO.BoardResponseDTO;
import com.hwanu.backend.DTO.CommentResponseDTO;
import com.hwanu.backend.DTO.PostReadResponseDTO;
import com.hwanu.backend.DTO.PostWriteDTO;
import com.hwanu.backend.domain.Board;
import com.hwanu.backend.domain.Comment;
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

import java.util.*;

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

    @Override
    public List<CommentResponseDTO> getCommentByBoardId(Long boardId) {

        List<Comment> rows = commentRepository.findByBoardIdRef(boardId, Sort.by(Sort.Direction.ASC, "commentId"));
//        log.info("조회된 데이터 : {}",rows);
        // 자식은 부모가 삭제되면 안보여줄것이기에 부모 후보들을 담기 위한 집함
        Map<Long, CommentResponseDTO> commentMap = new LinkedHashMap<>();
        for (Comment comment : rows) {
            if (comment.getParentId() == null) {
                commentMap.put(comment.getCommentId(), modelMapper.map(comment, CommentResponseDTO.class));
            } else {
                CommentResponseDTO parentComment = commentMap.get(comment.getParentId());
                if (parentComment != null) {
                    parentComment.getRecomment().add(modelMapper.map(comment, CommentResponseDTO.class));
                }
            }
        }

        List<CommentResponseDTO> commentResponseDTOS = new ArrayList<>(commentMap.values());

        return commentResponseDTOS;
    }

    @Override
    public Long insertBoard(PostWriteDTO postWriteDTO) {
        Board board = modelMapper.map(postWriteDTO, Board.class);
        Board saveBoard = boardRepository.save(board);
        Long getBoardId = saveBoard.getBoardId();
        return getBoardId;
    }
}
