package com.joojoo.api.jwt.infrastructure.jwt;

import com.joojoo.api.jwt.domain.service.JwtProvider;
import com.joojoo.global.common.request.auth.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtProviderImpl implements JwtProvider {

    @Value("${JWT_SECRET}")
    private String secretKey;

    @Value("${JWT_ACCESS_EXPIRATION}")
    private long accessTokenValidity;

    @Value("${JWT_REFRESH_EXPIRATION}")
    private long refreshTokenValidity;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    @Override
    public String createAccessToken(Long userId, String userName) {
        Claims claims = Jwts.claims().subject(userName).build();
        claims.put("userId", userId);
        return createToken(claims, accessTokenValidity);
    }

    @Override
    public String createRefreshToken(Long userId, String userName) {
        Claims claims = Jwts.claims().subject(userName).build();
        claims.put("userId", userId);
        return createToken(claims, refreshTokenValidity);
    }

    private String createToken(Claims claims, long validity) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        return Jwts.builder()
            .claims(claims)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact();
    }

    @Override
    public String extractBearerToken(HttpServletRequest request) { // Authorization 헤더에서 Bearer 토큰을 추출
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }

    @Override
    public void validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);

        } catch (ExpiredJwtException e) {
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        }
    }

    @Override
    public Authentication getAuthentication(String token) { // 토큰에서 User 정보를 꺼내서 Authentication 타입으로 반환
        Claims claims = Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();

        String username = claims.getSubject();
        Long userId = Long.valueOf(claims.get("userId").toString());

        CustomUserDetails user = new CustomUserDetails(userId, username);
        return new UsernamePasswordAuthenticationToken(user, "", List.of());
    }

    @Override
    public Date getExpiration(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getExpiration();
    }
}
