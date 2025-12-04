package com.joojoo.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 활성화
 */
@Configuration
@EnableJpaAuditing
@Profile("!test")   // 테스트 프로파일이 아닐 때만 활성화
public class JpaConfig {
}