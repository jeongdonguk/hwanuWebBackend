# 1단계: Gradle로 빌드
# 1. 빌드 도구 포함된 임시 이미지
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Gradle 캐시 최적화 (필요한 파일만 먼저 복사)
COPY build.gradle gradlew gradle /app/
COPY gradle/wrapper /app/gradle/wrapper

RUN ./gradlew dependencies --no-daemon

# 전체 소스 복사 후 bootJar 실행
COPY . .
RUN ./gradlew bootJar --no-daemon

# 1. Java 17 기반으로 실행
FROM openjdk:17-jdk-slim


# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 빌드된 JAR 파일을 컨테이너 내부로 복사
#COPY build/libs/*.jar app.jar
# 👇 이게 핵심! build 단계에서 만든 jar 파일을 가져와야 함
COPY --from=build /app/build/libs/*.jar app.jar

# 4. 컨테이너가 실행될 때 Spring Boot 실행
CMD ["java", "-jar", "app.jar"]
