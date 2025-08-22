package com.hwanu.backend.controller;

import com.hwanu.backend.DTO.MemberLoginDTO;
import com.hwanu.backend.DTO.MemberRegisterDTO;
import com.hwanu.backend.DTO.TokenResponseDTO;
import com.hwanu.backend.domain.Member;
import com.hwanu.backend.security.issuer.JwtIssuer;
import com.hwanu.backend.service.MemberService;
import com.hwanu.backend.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SimpleTimeZone;

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
    private final TokenService tokenService;
    private final JwtDecoder jwtDecoder;

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

        log.info("login 시도");

        TokenResponseDTO tokenResponseDTO = memberService.login(dto); // 로그인이 성공할 경우 엑세스 토큰, 리프레시 토큰을 발급

        String accessToken  = tokenResponseDTO.getAccessToken();
        String refreshToken = tokenResponseDTO.getRefreshToken();
        Jwt jwt = jwtDecoder.decode(accessToken);

        ResponseCookie refreshCookie = ResponseCookie.from("hwanuRefreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")            // 크로스 사이트 전송 허용(프론트/백 분리 환경)
                .path("/backendApi/auth/refresh")    // 리프레시는 인증 관련 경로에서만 사용하도록 축소
                .maxAge(Duration.ofDays(14)).build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
//        java.util.Map.of("email", email, "refreshed", true)
        Map<String, Object> userData = java.util.Map.of("email", jwt.getSubject(),
                                                        "memberId", jwt.getClaim("memberId"),
                                                        "nickname", jwt.getClaim("nickname"),
                                                        "role", jwt.getClaim("role") ,
                                                        "hwanuAccessToken", accessToken);
        log.info("로그인 성공 : " + dto.toString());
        return ResponseEntity.ok(userData);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃을 위해 사용. 사용시 refreshToken 삭제")
    public ResponseEntity<String> logout(@RequestBody Map<String, String> request, HttpServletResponse response) {

        String email = request.getOrDefault("email", "");
        log.info("로그아웃 요청 : " + email);
        tokenService.deleteRefreshToken(email);

        // ★ 쿠키 삭제는 maxAge(0) + 동일 옵션(sameSite/path)로 내려야 브라우저가 제거
//        ResponseCookie delAccess = ResponseCookie.from("hwanuAccessToken", "")
//                .httpOnly(true).secure(true).sameSite("None").path("/").maxAge(0).build();
        ResponseCookie delRefresh = ResponseCookie.from("hwanuRefreshToken", "")
                .httpOnly(true).secure(true).sameSite("Lax").path("/backendApi/auth/refresh").maxAge(0).build();

//        response.addHeader(HttpHeaders.SET_COOKIE, delAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, delRefresh.toString());

        return ResponseEntity.ok("로그아웃 성공");
    }


    // ========================
    // 토큰 리프레시: 새 액세스 토큰 재발급
    // ========================
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        // 1) 쿠키에서 리프레시 토큰 추출
        log.info("refrsh 실행");
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie c : request.getCookies()) {
//                log.info(c.getName());
                if ("hwanuRefreshToken".equals(c.getName())) {
                    refreshToken = c.getValue();
                    break;
                }
            }
        }
//        log.info("refresh token : " + refreshToken);
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No refresh token");
        }

        // 2) 리프레시 토큰 검증 + 이메일 조회
        Map<String, String> newAccessTokenAndEmail = tokenService.refreshAccessToken(refreshToken);

        if (newAccessTokenAndEmail.containsKey("error")) {
            ResponseCookie delRefresh = ResponseCookie.from("hwanuRefreshToken", "")
                    .httpOnly(true).secure(true).sameSite("Lax").path("/backendApi/auth/refresh").maxAge(0).build();
            response.addHeader(HttpHeaders.SET_COOKIE, delRefresh.toString());
            log.info("error : " + newAccessTokenAndEmail.get("error"));
        }

        return ResponseEntity.ok(newAccessTokenAndEmail);
    }


    @GetMapping("/me")
    @Operation(summary = "로그인 중 사용자 정보", description = "로그인한 사용자의 이메일 정보")
    public ResponseEntity<?> me(@AuthenticationPrincipal Jwt jwt) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (jwt == null) {
            log.info("로그인 정보 없음");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 정보 없음");
        }
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", jwt.getSubject());
        userData.put("memberId", jwt.getClaim("memberId"));
        userData.put("nickname", jwt.getClaim("nickname"));
        userData.put("role", jwt.getClaim("role"));
        log.info("email : "+ jwt.getSubject());
        return ResponseEntity.ok(userData);
    }

//    @GetMapping("/test")
//    @Operation(summary = "토큰 테스트", description = "토큰의 정상 작동 여부는 위의 인증 버튼에서 하세요.")
//    public ResponseEntity<String> testAuth(@RequestHeader("Authorization") String token) {
//        return ResponseEntity.ok("인증 성공 : " + token);
//    }

}
