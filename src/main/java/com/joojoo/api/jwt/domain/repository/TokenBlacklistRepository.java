package com.joojoo.api.jwt.domain.repository;

public interface TokenBlacklistRepository {
    // 로그아웃 시 AccessToken 블랙리스트에 저장
    void add(String accessToken, Long expiration);

    // 블랙리스트에 들어있는 AccessToken인지 검증
    boolean isBlacklisted(String token);

}
