package com.joojoo.api.jwt.application;

import com.joojoo.api.jwt.domain.model.entity.Token;
import com.joojoo.api.jwt.domain.repository.TokenRepository;
import com.joojoo.api.jwt.domain.service.JwtProvider;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.global.common.util.TokenExpirationUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JwtTokenUseCaseImpl implements JwtTokenUseCase {

    private final TokenRepository tokenRepository;
    private final JwtProvider jwtProvider;

    @Override
    @Transactional
    public String createAndSaveRefreshToken(Long userId, String userName, User user) {
        String refreshToken = jwtProvider.createRefreshToken(userId, userName);
        Date expirationDate = jwtProvider.getExpiration(refreshToken);
        LocalDateTime expiresAt = TokenExpirationUtil.toLocalDateTime(expirationDate);

        tokenRepository.findByUserId(userId)
            .ifPresentOrElse(
                existingToken -> {
                    existingToken.updateRefreshToken(refreshToken, expiresAt);  // 기존 토큰이 있으면, 업데이트
                    tokenRepository.save(existingToken);
                },
                () -> tokenRepository.save(Token.create(user, refreshToken, expiresAt)) // 토큰이 없으면, 새로 저장
            );
        return refreshToken;
    }

    @Override
    public String createAccessToken(Long userId, String userName) {
        return jwtProvider.createAccessToken(userId, userName);
    }

    @Override
    @Transactional
    public void clearUserTokens(Long userId, HttpServletRequest request) {
        String accessToken = jwtProvider.extractBearerToken(request);
        Long expiration = jwtProvider.getRemainingTime(accessToken);

        // TODO : Redis에 키 값으로 accessToken 넣고 만료시간 expiration으로 정의

        tokenRepository.delete(userId);
    }

}
