package com.joojoo.api.jwt.domain.model.entity;

import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.global.exception.handleException.jwt.TokenVerificationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Builder
    public Token(User user, String refreshToken, LocalDateTime expiresAt) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }

    public static Token create(User user, String refreshToken, LocalDateTime expiresAt) {
        return Token.builder()
            .user(user)
            .refreshToken(refreshToken)
            .expiresAt(expiresAt)
            .build();
    }

    public void updateRefreshToken(String refreshToken, LocalDateTime expiresAt) {
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }

    public void validateSameToken(String refreshToken) {
        if (!this.refreshToken.equals(refreshToken)) {
            throw new TokenVerificationException();
        }
    }
}
