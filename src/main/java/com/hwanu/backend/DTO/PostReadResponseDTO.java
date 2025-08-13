package com.hwanu.backend.DTO;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostReadResponseDTO {

    private Long boardId;
    private Long memberId;
    private String email;
    private String nickname;
    private String title;
    private String content;
    private int viewCnt = 0;
    private int likeCnt = 0;
    private String notiYN = "N";
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
