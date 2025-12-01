package com.joojoo.api.jwt.domain.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import java.util.Date;

public interface JwtProvider {
    String createAccessToken(Long userId, String userName);
    String createRefreshToken(Long userId, String userName);

    // 해더에서 토큰 추출
    String extractBearerToken(HttpServletRequest request);
    // 토큰 유효성 검사
    void validateToken(String token);
    // 토큰에서 인증 정보 조회
    Authentication getAuthentication(String token);
    // 로그아웃 구현 시 필요: 토큰의 남은 유효시간(ms) 조회
    Date getExpiration(String token);
}
