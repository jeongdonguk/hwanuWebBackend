# 화누 프로젝트 - Backend

**화누(Whanu)**는 반려견 보호자를 위한 커뮤니티 및 케어 서비스 플랫폼입니다.  
사용자는 반려견과 관련된 정보 공유, 실시간 채팅, 예약, 일정 알림, AI 챗봇 상담 등을 앱을 통해 경험할 수 있습니다.

본 저장소는 화누의 **백엔드 API 서버(Spring Boot 기반)**와 **배치 처리**, **AI 챗봇 서버(FastAPI)**까지 포함한 전체 백엔드 시스템을 다룹니다.

---
## 📦 프로젝트 버전 정보

| 항목               | 버전           |
|--------------------|----------------|
| Java               | 17 (JDK 17)    |
| Spring Boot        | 3.4.3          |
| Gradle Plugin      | 8.x (권장)     |
| 빌드 도구          | Gradle         |
| API 문서화 도구     | SpringDoc OpenAPI 2.8.5 |

---

## ⚙️ 기술 스택

### 📌 핵심 프레임워크 & 라이브러리
- `spring-boot-starter-web` – RESTful API 서버
- `spring-boot-starter-security` – 인증/인가
- `spring-boot-starter-oauth2-client` – 소셜 로그인 (Google, Kakao, Naver)
- `spring-boot-starter-oauth2-resource-server` – JWT 기반 인증 검증
- `spring-boot-starter-data-jpa` – JPA + Hibernate
- `springdoc-openapi-starter-webmvc-ui:2.8.5` – Swagger UI + OpenAPI 문서 자동화
- `modelmapper` – DTO 변환
- `lombok` – 보일러플레이트 코드 제거

### 🗃️ 인프라/외부 시스템
- **MySQL 8+** - 데이터 저장
- **Redis** – 세션 캐싱 및 일정 알림 큐
- **Elasticsearch** – 게시판 검색
- **Kafka** – 사용자 이벤트 로그 처리
- **MinIO** – 파일 저장
- **ChromaDB** – RAG 벡터 검색
- **FastAPI + GPT4** – AI 챗봇 API

---

## 📡 대표 API

현재 구현된 API는 아래와 같습니다.  
(추후 확장될 API는 직접 추가해주세요.)

### 1. 🔐 인증 및 회원 관리

| 메서드 | 엔드포인트        | 설명                          |
|--------|-------------------|-------------------------------|
| POST   | `/auth/login`     | 이메일/비밀번호 로그인         |
| POST   | `/auth/register`    | 일반 회원가입                  |
| GET    | `/auth/logout` | 로그아웃, refresh토큰 삭제 |
| GET    | `/auth/refresh` | access 토큰 만료시 refresh토큰으로 재발급 요청 |
| GET    | `/auth/me`       | 로그인된 사용자 정보 조회       |

---

### 2. 📝 커뮤니티 게시판

| 메서드 | 엔드포인트           | 설명               |
|--------|----------------------|--------------------|
| GET    | `/board/list`             | 게시글 목록 조회    |
| GET    | `/board/public`    | 게시글 목록 조회(비회원 첫 페이지)    |
| GET   | `/board/postRead`             | 글상세 페이지        |
| GET    | `/board/postComment`    | 글상세 페이지 댓글정보        |

> 🔍 Elasticsearch 기반 검색 API는 `/board?search=...` 형태로 통합 구현 예정입니다.


