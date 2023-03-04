package com.example.springdatajpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@Configuration @EnableJpaAuditing
public class JpaConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        // 실무나 실제로는 이곳에 스프링 시큐리티를 사요하여 Session 이나 JWT 에 담긴 Authentication 을 꺼내서 사용함.
        // 현재는 그냥 uuid 로 대체
        return () -> Optional.of(UUID.randomUUID().toString());
    }
}
