package com.joojoo.api.user.presentation.controller;

import com.joojoo.api.user.application.UserService;
import com.joojoo.api.user.application.auth.AuthSocialService;
import com.joojoo.api.user.presentation.dto.request.AuthCodeDto;
import com.joojoo.api.user.presentation.dto.response.LoginResponse;
import com.joojoo.global.common.request.auth.CustomUserDetails;
import com.joojoo.global.common.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final AuthSocialService authSocialService;
    private final UserService userService;

    @PostMapping("/kakao/login")
    public BaseResponse<LoginResponse> kakaoLogin(@RequestBody AuthCodeDto payload) {
        return BaseResponse.ok(authSocialService.kakaoSocialLogin(payload.getCode()));
    }

    @PostMapping("/apple/login")
    public BaseResponse<LoginResponse> appleLogin(@RequestBody AuthCodeDto payload) {
        return BaseResponse.ok(authSocialService.appleSocialLogin(payload.getCode()));
    }

    @PostMapping("/withdraw")
    public BaseResponse<String> withdraw(@AuthenticationPrincipal CustomUserDetails user, HttpServletRequest request) {
        authSocialService.withdraw(user.getUserId(), request);
        return BaseResponse.ok("회원 탈퇴가 완료되었습니다.");
    }

    @PostMapping("/logout")
    public BaseResponse<String> logout(@AuthenticationPrincipal CustomUserDetails user, HttpServletRequest request) {
        userService.logout(user.getUserId(), request);
        return BaseResponse.ok("로그아웃이 완료되었습니다.");
    }
}
