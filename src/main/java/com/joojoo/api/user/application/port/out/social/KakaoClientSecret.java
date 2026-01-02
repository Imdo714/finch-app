package com.joojoo.api.user.application.port.out.social;

import com.joojoo.api.user.presentation.dto.request.kakao.AccessTokenDto;
import com.joojoo.api.user.presentation.dto.request.kakao.KakaoUserDto;

public interface KakaoClientSecret {
    // 카카오 서버로 부터 토큰 받기
    AccessTokenDto getKakaoAccessToken(String code);

    // 카카오 사용자 정보 받기
    KakaoUserDto getUserInfoFromKakao(String accessToken);

    // 카카오 RefreshToken으로 새로운 AccessToken 받기
    String renewKakaoAccessToken(String socialRefresh);

    // 카카오 탈퇴 요청
    void unlinkKakaoUser(String kakaoAccessToken);
}
