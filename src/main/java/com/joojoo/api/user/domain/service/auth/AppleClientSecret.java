package com.joojoo.api.user.domain.service.auth;

import com.joojoo.api.user.presentation.dto.request.apple.AppleTokenResponse;

import java.util.Map;

public interface AppleClientSecret {
    // Client Secret 생성
    String createClientSecret();
    // 애플 서버로 토큰 요청
    AppleTokenResponse requestAppleToken(String authorizationCode, String clientSecret);
    // idToken 파싱
    Map<String, Object> getAppleUserIdFromIdToken(String idToken);
}
