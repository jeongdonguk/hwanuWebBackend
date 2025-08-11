package com.hwanu.backend.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration  // 스프링 설정 클래스임을 나타냄 (Bean 등록 목적)
public class RootConfig {

    @Bean // 스프링 컨테이너에 ModelMapper 객체를 Bean으로 등록
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper(); // ModelMapper 인스턴스 생성
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true) // 필드 이름이 동일하다면 직접 접근(Setter 없이)해서 매핑 가능하도록 설정
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE) // private 필드에도 접근할 수 있도록 허용
                .setMatchingStrategy(MatchingStrategies.LOOSE); // 매핑 전략을 LOOSE로 설정 (이름이 완전히 일치하지 않아도 유사하면 매핑) // 예: "userName" ↔ "username" 가능
        return modelMapper;
    }
}