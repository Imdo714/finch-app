package com.joojoo.api.user.application.port.out.social;

import com.joojoo.api.user.presentation.dto.request.apple.AppleTokenResponse;
import com.joojoo.api.user.presentation.dto.request.apple.AppleUserInfo;

import java.util.Map;

public interface AppleClientSecret {
    // Client Secret 생성
    String createClientSecret();
    // 애플 서버로 토큰 요청
    AppleTokenResponse requestAppleToken(String authorizationCode, String clientSecret);
    // idToken 파싱
    Map<String, Object> getAppleUserIdFromIdToken(String idToken);
    // 애플 서버에 계정 탈퇴 요청
    void sendRevokeRequest(String clientSecret, String socialRefreshToken);

    /** idToken으로 사용자 정보 추출하는 메서드 */
    AppleUserInfo getAppleUserInfo(String idToken);

    String getPackageName();

    String getScheme();
}
