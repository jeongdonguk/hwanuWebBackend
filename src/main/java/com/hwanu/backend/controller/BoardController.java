package com.hwanu.backend.controller;

import com.hwanu.backend.DTO.BoardResponseDTO;
import com.hwanu.backend.DTO.PostReadResponseDTO;
import com.hwanu.backend.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor
@Tag(name = "게시판", description = "게시판 관련 처리")
public class BoardController {

    private final BoardService boardService;

    // 로그인 무관으로 변경(2025.08.11)
    @Operation(summary = "로그인 후 게시글 목록", description = "로그인 후에 사용자가 게시글 정보들을 받을 때 사용")
    @GetMapping("/list")
    public ResponseEntity<Page<BoardResponseDTO>> getBoardList(Pageable pageable){
        Page<BoardResponseDTO> boardList = boardService.getAllBoards(pageable);
        return ResponseEntity.ok(boardList);
    }

    @Operation(summary = "로그인 전 기본 게시글 목록", description = "로그인을 하지 않아도 첫페이지는 보여줌")
    @GetMapping("/public")
    public ResponseEntity<Page<BoardResponseDTO>> getPublicBoardList(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<BoardResponseDTO> boardList = boardService.getAllBoards(pageable);
        return ResponseEntity.ok(boardList);
    }

    // 글 읽기 페이지
    @Operation(summary = "글상세 페이지", description = "게시판 글을 클릭시 볼 수 있는 상세페이지 사용 데이터")
    @GetMapping("/postRead")
    public ResponseEntity<PostReadResponseDTO> getPostRead(@RequestParam Long boardId){
//        log.info("글 조회 : ");
        log.info("글 조회 : {}",boardId);
        PostReadResponseDTO postData = boardService.getBoardById(boardId);
        return ResponseEntity.ok(postData);
    }


}
