# 1. Java 17 기반으로 실행
FROM openjdk:17-jdk-slim

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 빌드된 JAR 파일을 컨테이너 내부로 복사
COPY build/libs/*.jar app.jar

# 4. 컨테이너가 실행될 때 Spring Boot 실행
CMD ["java", "-jar", "app.jar"]
