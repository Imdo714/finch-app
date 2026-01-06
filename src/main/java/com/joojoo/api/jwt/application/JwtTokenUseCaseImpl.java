package com.joojoo.api.jwt.application;

import com.joojoo.api.jwt.domain.model.entity.Token;
import com.joojoo.api.jwt.domain.repository.TokenBlacklistRepository;
import com.joojoo.api.jwt.domain.repository.TokenRepository;
import com.joojoo.api.jwt.domain.service.JwtProvider;
import com.joojoo.api.jwt.presentation.dto.response.ReissueTokenResponse;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.global.common.request.auth.CustomUserDetails;
import com.joojoo.global.util.token.TokenExpirationUtil;
import com.joojoo.global.exception.handleException.jwt.RefreshTokenExpiredException;
import com.joojoo.global.exception.handleException.jwt.TokenVerificationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JwtTokenUseCaseImpl implements JwtTokenUseCase {

    private final TokenRepository tokenRepository;
    private final JwtProvider jwtProvider;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Override
    @Transactional
    public String createAndSaveRefreshToken(Long userId, String userName, User user, String role) {
        String refreshToken = jwtProvider.createRefreshToken(userId, userName, role);
        LocalDateTime expiresAt = TokenExpirationUtil.toLocalDateTime(jwtProvider.getExpiration(refreshToken));

        registerRefreshToken(user, refreshToken, expiresAt);
        return refreshToken;
    }

    private void registerRefreshToken(User user, String refreshToken, LocalDateTime expiresAt) {
        tokenRepository.findByUserId(user.getId())
                .ifPresentOrElse(
                        existingToken -> {
                            existingToken.updateRefreshToken(refreshToken, expiresAt);
                        },
                        () -> {
                            tokenRepository.save(Token.create(user, refreshToken, expiresAt));
                        }
                );
    }

    @Override
    public String createAccessToken(Long userId, String userName, String role) {
        return jwtProvider.createAccessToken(userId, userName, role);
    }

    @Override
    @Transactional
    public void clearUserTokens(Long userId, HttpServletRequest request) {
        String accessToken = jwtProvider.extractBearerToken(request);
        Long expiration = jwtProvider.getRemainingTime(accessToken);

        tokenBlacklistRepository.add(accessToken, expiration); // AccessToken 블랙리스트에 저장
        tokenRepository.delete(userId); // DB에서 RefreshToken 삭제
    }

    @Override
    public ReissueTokenResponse reissueAccessToken(String refreshToken) {
        validateToken(refreshToken);
        Authentication authentication = jwtProvider.getAuthentication(refreshToken);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Token storedToken = tokenRepository.findByUserId(userDetails.getUserId())
                .orElseThrow(RefreshTokenExpiredException::new);

        storedToken.validateSameToken(refreshToken);
        return new ReissueTokenResponse(jwtProvider.createAccessToken(userDetails.getUserId(), userDetails.getUsername(), userDetails.getRole()));
    }

    private void validateToken(String refreshToken) {
        try {
            jwtProvider.validateToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new RefreshTokenExpiredException();
        } catch (JwtException e) {
            throw new TokenVerificationException();
        }
    }

}
