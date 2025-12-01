package com.joojoo.api.user.domain.service.auth;

import com.joojoo.api.user.presentation.dto.request.kakao.AccessTokenDto;
import com.joojoo.api.user.presentation.dto.request.kakao.KakaoUserDto;

public interface KakaoClientSecret {
    // 카카오 서버로 부터 토큰 받기
    AccessTokenDto getKakaoAccessToken(String code);
    // 카카오 사용자 정보 받기
    KakaoUserDto getUserInfoFromKakao(String accessToken);
}
