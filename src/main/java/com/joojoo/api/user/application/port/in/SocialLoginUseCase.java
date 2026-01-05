package com.joojoo.api.user.application.port.in;

import com.joojoo.api.user.presentation.dto.request.AuthTokenDto;
import com.joojoo.api.user.presentation.dto.response.LoginResponse;

public interface SocialLoginUseCase {
    LoginResponse kakaoWebSocialLogin(String code);
    LoginResponse kakaoAppSocialLogin(AuthTokenDto authTokenDto);
    LoginResponse appleSocialLogin(String code);

    LoginResponse appleWebLogin(String code);
}
