package com.hwanu.backend.controller;

import com.hwanu.backend.repository.MemberRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Log4j2
@RequiredArgsConstructor
@Tag(name = "테스트", description = "테스트용 api")
public class TestController {

    private final MemberRepository memberRepository;

    @GetMapping("/timeTest")
    public ResponseEntity<String> timeTest() {
        String times = memberRepository.getTime();
        return ResponseEntity.ok(times);
    }

}
