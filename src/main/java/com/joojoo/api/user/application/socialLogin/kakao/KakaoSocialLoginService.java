package com.joojoo.api.user.application.socialLogin.kakao;

import com.joojoo.api.user.presentation.dto.response.LoginResponse;

public interface KakaoSocialLoginService {
    LoginResponse kakaoSocialLogin(String code);
}
