package com.joojoo.api.user.application.auth;

import com.joojoo.api.user.presentation.dto.request.AuthTokenDto;
import com.joojoo.api.user.presentation.dto.response.LoginResponse;

public interface AuthSocialService {
    LoginResponse kakaoWebSocialLogin(String code);
    LoginResponse kakaoAppSocialLogin(AuthTokenDto authTokenDto);
    LoginResponse appleSocialLogin(String code);
}
