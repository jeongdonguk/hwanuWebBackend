package com.hwanu.backend.repository;

import com.hwanu.backend.domain.Board;
import com.hwanu.backend.domain.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Long countByBoard(Board board);

    List<Comment> findByBoardIdRef(Long boardIdRef, Sort sort);
}
