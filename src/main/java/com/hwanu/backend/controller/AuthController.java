package com.hwanu.backend.controller;

import com.hwanu.backend.DTO.MemberLoginDTO;
import com.hwanu.backend.DTO.MemberRegisterDTO;
import com.hwanu.backend.domain.Member;
import com.hwanu.backend.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Log4j2
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "회원가입을 위해 사용")
    public ResponseEntity<String> register(@Valid @RequestBody MemberRegisterDTO dto) {
        String response = memberService.register(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인을 위해 사용")
    public ResponseEntity<String> login(@Valid @RequestBody MemberLoginDTO dto) {
        String response = memberService.login(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    @Operation(summary = "토큰 테스트", description = "토큰의 정상 작동 여부를 테스트 합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<String> testAuth(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok("인증 성공 : " + token);
    }

}
