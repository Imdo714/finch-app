package com.joojoo.api.user.application.auth;

import com.joojoo.api.user.presentation.dto.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthSocialService {
    LoginResponse kakaoSocialLogin(String code);
    LoginResponse appleSocialLogin(String code);
    void withdraw(Long userId, HttpServletRequest request);
}
