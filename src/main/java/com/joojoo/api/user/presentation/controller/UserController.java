package com.joojoo.api.user.presentation.controller;

import com.joojoo.global.common.response.ApiResponse;
import com.joojoo.api.user.application.socialLogin.kakao.KakaoSocialLoginService;
import com.joojoo.api.user.presentation.dto.request.AuthCodeDto;
import com.joojoo.api.user.presentation.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final KakaoSocialLoginService kakaoSocialLoginService;

    @PostMapping("/kakao/login")
    public ApiResponse<LoginResponse> kakaoLogin(@RequestBody AuthCodeDto payload) {
        return ApiResponse.ok(kakaoSocialLoginService.kakaoSocialLogin(payload.getCode()));
    }
}
