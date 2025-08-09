package com.hwanu.backend.controller;

import com.hwanu.backend.DTO.MemberLoginDTO;
import com.hwanu.backend.DTO.MemberRegisterDTO;
import com.hwanu.backend.repository.MemberRepository;
import com.hwanu.backend.repository.RefreshTokenRepository;
import com.hwanu.backend.security.JwtUtil;
import com.hwanu.backend.service.MemberService;
import com.hwanu.backend.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

//보너스: 실무에서 자주 쓰는 패턴
//1) 쿠키에 JWT 넣을 때
//XSS 방지: httpOnly(true)
//HTTPS 강제: secure(true)
//CSRF 고려: SameSite=Lax/Strict (필요 시)

@RestController
@RequestMapping("/auth")
@Log4j2
@RequiredArgsConstructor
@Tag(name = "로그인 인증관련", description = "로그인 인증관련 api")
public class AuthController {

    private final MemberService memberService; // 회원가입 로직 처리
    private final TokenService tokenService; // 리프레시 토큰 관련 처리
    private final JwtUtil jwtUtil; // jwt토큰 발급 관련

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
    public ResponseEntity<?> login(@Valid @RequestBody
                                       MemberLoginDTO dto,
                                   HttpServletResponse response ) { // response를 직접 제어하기 위해

        String accessToken = memberService.login(dto); // 로그인이 성공할 경우 엑세스 토큰을 발급
        ResponseCookie jwtCookie = ResponseCookie.from("hwanuAccessToken", accessToken)
                        .httpOnly(false)
                        .secure(true)
                        .path("/")
                        .maxAge(Duration.ofDays(14)).build();
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        Map<String, String> userData = Map.of("email", dto.getEmail());
        log.info("로그인 성공 : " + dto.toString());
        return ResponseEntity.ok(userData);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃을 위해 사용. 사용시 refreshToken 삭제")
    public ResponseEntity<String> logout(@RequestBody Map<String, String> request, HttpServletResponse response) {

        String email = request.getOrDefault("email", "");
        log.info("로그아웃 요청 : " + email);
        tokenService.deleteRefreshToken(email);

        // 세션을 사용하지 않아서 필요업음
//        SecurityContextHolder.clearContext();
//        log.info("securityContextholder 초기화");

        // 엑세스토큰 null로 초기화
        ResponseCookie jwtCookie = ResponseCookie.from("hwanuAccessToken", null)
                .httpOnly(false)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(14)).build();

        // 헤더에 전달
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        return ResponseEntity.ok("로그아웃 성공");
    }

    @GetMapping("/me")
    @Operation(summary = "로그인 중 사용자 정보", description = "로그인한 사용자의 이메일 정보")
    public ResponseEntity<?> me(@AuthenticationPrincipal UserDetails userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (userDetails == null) {
            log.info("로그인 정보 없음");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 정보 없음");
        }
        Map<String, String> userData = new HashMap<>();
        userData.put("email", userDetails.getUsername());
        log.info("email : "+ userDetails.getUsername());
        return ResponseEntity.ok(userData);
    }

//    @GetMapping("/test")
//    @Operation(summary = "토큰 테스트", description = "토큰의 정상 작동 여부는 위의 인증 버튼에서 하세요.")
//    public ResponseEntity<String> testAuth(@RequestHeader("Authorization") String token) {
//        return ResponseEntity.ok("인증 성공 : " + token);
//    }

}
