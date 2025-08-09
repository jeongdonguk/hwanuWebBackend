package com.hwanu.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass // 상속받는 엔티티 클래스에 맵핑정보 자동 전달
@EntityListeners(AuditingEntityListener.class) // JPA audicting 기능 (생성/수정 날짜를 자동 관리)
@Getter
abstract class BaseEntity {

    @CreatedDate // 엔티티 처음 저장될때 자동으로 값 할당 -JPA audicting
    @Column(name = "created_at", updatable = false) // db컬럼명 지정 및 업데이트 여부 => 수정불가
    private LocalDateTime createdAt;


    @LastModifiedDate // 자동 업데이트 되도록
    @Column(name = "updated_at", updatable = true) // 업데이트시마다 수정됨
    private LocalDateTime updatedAt;
}
