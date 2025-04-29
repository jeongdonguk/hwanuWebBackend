package com.hwanu.backend.controller;

import com.hwanu.backend.DTO.BoardResponseDTO;
import com.hwanu.backend.service.BoardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor
@Tag(name = "게시판", description = "게시판 관련 처리")
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/list")
    public ResponseEntity<Page<BoardResponseDTO>> getBoardList(Pageable pageable){
        Page<BoardResponseDTO> boardList = boardService.getAllBoards(pageable);
        return ResponseEntity.ok(boardList);
    }
}
