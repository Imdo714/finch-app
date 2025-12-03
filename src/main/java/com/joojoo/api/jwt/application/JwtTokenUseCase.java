package com.joojoo.api.jwt.application;

import com.joojoo.api.user.domain.model.entity.User;

public interface JwtTokenUseCase {
    // RefreshToken 생성 및 DB 저장
    String createAndSaveRefreshToken(Long userId, String userName, User user);

    // AccessToken 생성
    String createAccessToken(Long userId, String userName);
}
