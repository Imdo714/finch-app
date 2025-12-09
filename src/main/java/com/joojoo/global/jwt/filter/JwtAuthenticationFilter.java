package com.joojoo.global.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joojoo.api.jwt.domain.repository.TokenBlacklistRepository;
import com.joojoo.api.jwt.domain.service.JwtProvider;
import com.joojoo.global.common.response.BaseResponse;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    private final List<String> excludedUrls = List.of( // 인증 제외 URL
        "/user/kakao/login", "/user/apple/login", "/add"
    );

    public JwtAuthenticationFilter(JwtProvider jwtProvider, TokenBlacklistRepository tokenBlacklistRepository) {
        this.jwtProvider = jwtProvider;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("================ doFilterInternal Action ================");
        log.info("request.getRequestURI() = {}", request.getRequestURI());

        try {
            if (isExcludedUrl(request)) { // 필터 제외 URL이면 바로 다음 필터로 진행
                filterChain.doFilter(request, response);
                return;
            }
            authenticateIfTokenExists(request); // JWT 인증 처리 후 SecurityContext에 User정보 저장
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다.");
        } catch (JwtException e) {
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, e.getMessage());
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

    // 인증 제외 URL인지 검증
    private boolean isExcludedUrl(HttpServletRequest request) {
        String path = request.getRequestURI();
        return excludedUrls.contains(path);
    }

    private void setErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BaseResponse<Object> baseResponse = BaseResponse.of(status, message);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(baseResponse);

        response.getWriter().write(json);
    }
}
