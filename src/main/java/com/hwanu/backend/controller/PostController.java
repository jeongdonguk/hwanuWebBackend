package com.hwanu.backend.controller;

import com.hwanu.backend.DTO.CommentWriteDTO;
import com.hwanu.backend.DTO.PostWriteDTO;
import com.hwanu.backend.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/post")
@Log4j2
@RequiredArgsConstructor
@Tag(name = "글, 댓글 CUD 관련", description = "게시판 글쓰기, 댓글 쓰기, 수정, 삭제 관련 처리")
public class PostController {

    private final BoardService boardService;

    @Operation(summary = "글쓰기", description = "글작성 요청")
    @PostMapping("/writePost")
    public ResponseEntity<?> writePost(@Valid @RequestBody PostWriteDTO postWriteDTO,
                                       @AuthenticationPrincipal Jwt jwt){
        log.info("postWriteDTO : {}", postWriteDTO);
        postWriteDTO.setEmail(jwt.getSubject());
        postWriteDTO.setNickname(jwt.getClaimAsString("nickname"));
        postWriteDTO.setMemberId(jwt.getClaim("memberId"));
        Long newBoardId = boardService.insertBoard(postWriteDTO);
        Map<String, Long> result = new HashMap<>();
        result.put("boardId", newBoardId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "댓글 쓰기", description = "댓글 작성 요청")
    @PostMapping("/writeComment")
    public ResponseEntity<?> writeComment(@Valid @RequestBody CommentWriteDTO commentWriteDTO,
                                       @AuthenticationPrincipal Jwt jwt){
        log.info("postWriteDTO : {}", commentWriteDTO);
        commentWriteDTO.setEmail(jwt.getSubject());
        commentWriteDTO.setNickname(jwt.getClaimAsString("nickname"));
        commentWriteDTO.setMemberId(jwt.getClaim("memberId"));
        Long commentId = boardService.insertComment(commentWriteDTO);
        Map<String, Long> result = new HashMap<>();
        result.put("commentId", commentId);
        return ResponseEntity.ok(result);
    }
}
