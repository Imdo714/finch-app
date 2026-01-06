package com.joojoo.global.config;

import com.joojoo.api.jwt.domain.repository.TokenBlacklistRepository;
import com.joojoo.api.jwt.domain.service.JwtProvider;
import com.joojoo.global.exception.authentication.CustomAccessDeniedHandler;
import com.joojoo.global.exception.authentication.CustomAuthenticationEntryPoint;
import com.joojoo.global.jwt.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtProvider jwtProvider;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource, JwtProvider jwtProvider, TokenBlacklistRepository tokenBlacklistRepository) {
        this.corsConfigurationSource = corsConfigurationSource;
        this.jwtProvider = jwtProvider;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider, tokenBlacklistRepository);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/user/kakao/login", "/user/apple/login", "/user/apple/web/login", "/user/apple/android/login", "/token/reissue").permitAll()
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/health/**").permitAll()

                    // 가입 대기자(PENDING)만 접근 가능한 API
                    .requestMatchers("/user/consent").hasRole("PENDING")

                    .anyRequest().hasAnyRole("USER", "ADMIN")
            )

            // Security 에서 걸린 애들 즉, authenticated()에 로그인을 안한 애들은 예외처리
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // 401
                    .accessDeniedHandler(new CustomAccessDeniedHandler()) // 403
            )

            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }

}
