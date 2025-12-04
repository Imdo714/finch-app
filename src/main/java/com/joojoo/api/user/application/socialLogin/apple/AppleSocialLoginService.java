package com.joojoo.api.user.application.socialLogin.apple;

import com.joojoo.api.user.presentation.dto.response.LoginResponse;

public interface AppleSocialLoginService {
    LoginResponse appleSocialLogin(String identityToken);
}
