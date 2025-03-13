package com.hwanu.backend.controller;

import com.hwanu.backend.DTO.MemberLoginDTO;
import com.hwanu.backend.DTO.MemberRegisterDTO;
import com.hwanu.backend.service.MemberService;
import com.hwanu.backend.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Log4j2
@RequiredArgsConstructor
@Tag(name = "로그인 인증관련", description = "로그인 인증관련 api")
public class AuthController {

    private final MemberService memberService;
    private final TokenService tokenService;

    // 회원가입
    // 입력 : 회원가입정보 , 출력 : 회원가입 성공여부
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "회원가입을 위해 사용")
    public ResponseEntity<String> register(@Valid @RequestBody MemberRegisterDTO dto) {
        String response = memberService.register(dto);
        return ResponseEntity.ok(response);
    }

    // 로그인
    // 입력 : 멤버정보 , 출력 : 엑세스 토큰
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인을 위해 사용")
    public ResponseEntity<String> login(@Valid @RequestBody MemberLoginDTO dto) {
        String response = memberService.login(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃을 위해 사용. 사용시 refreshToken 삭제")
    public ResponseEntity<String> logout(@RequestBody Map<String, String> request) {
        String email = request.getOrDefault("email", "");
        tokenService.deleteRefreshToken(email);
        return ResponseEntity.ok(String.format("%s 로그아웃 성공", email));
    }

    @GetMapping("/test")
    @Operation(summary = "토큰 테스트", description = "토큰의 정상 작동 여부는 위의 인증 버튼에서 하세요.")
    public ResponseEntity<String> testAuth(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok("인증 성공 : " + token);
    }

}
