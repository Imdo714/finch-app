package com.joojoo.global.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joojoo.api.jwt.domain.repository.TokenBlacklistRepository;
import com.joojoo.api.jwt.domain.service.JwtProvider;
import com.joojoo.api.common.domain.response.ErrorResponse;
import com.joojoo.global.exception.handleException.redis.RedisConnectionFailException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final List<String> excludedUrls = List.of(
            "/user/kakao/login",
            "/user/apple/login",
            "/user/apple/web/login",
            "/user/apple/android/login",
            "/token/reissue",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/health/**"
    );

    public JwtAuthenticationFilter(JwtProvider jwtProvider, TokenBlacklistRepository tokenBlacklistRepository) {
        this.jwtProvider = jwtProvider;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    // shouldNotFilter 메서드가 true를 반환하면 doFilterInternal메서드는 실행되지 않음
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return excludedUrls.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("================ doFilterInternal Action ================");
        log.info("request.getRequestURI() = {}", request.getRequestURI());

        try {
            authenticateIfTokenExists(request); // JWT 인증 처리 후 SecurityContext에 User정보 저장
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다.");
        } catch (JwtException e) {
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (RedisConnectionFailException e) {
            log.error("Redis 장애 발생");
            setErrorResponse(response, HttpStatus.SERVICE_UNAVAILABLE, "시스템 Redis가 접속 불량이여 점검 중입니다.");
        } catch (Exception e) {
            log.error("Unknown error in JwtAuthenticationFilter", e);
            setErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류 발생");
        }
    }

    // JWT 토큰이 있을 경우 인증 처리
    private void authenticateIfTokenExists(HttpServletRequest request) {
        String token = jwtProvider.extractBearerToken(request);
        if (token == null) return;

        if (tokenBlacklistRepository.isBlacklisted(token)) {
            throw new JwtException("로그아웃된 토큰입니다.");
        }

        jwtProvider.validateToken(token);
        Authentication auth = jwtProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void setErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // BaseResponse -> ErrorResponse 로 교체
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.value())
                .message(message)           // 예: "시스템 점검 중입니다 (Redis)."
                .code(status.name())        // 예: "SERVICE_UNAVAILABLE" (HTTP 상태 이름을 코드로 사용)
                .detailMessage(message)     // 상세 메시지도 동일하게 넣음
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(errorResponse);

        response.getWriter().write(json);
    }
}
