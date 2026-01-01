package com.joojoo.api.user.presentation.controller;

import com.joojoo.api.user.application.UserService;
import com.joojoo.api.user.application.auth.AuthSocialService;
import com.joojoo.api.user.application.port.in.LogoutUseCase;
import com.joojoo.api.user.application.port.in.WithdrawUserUseCase;
import com.joojoo.api.user.presentation.dto.request.AuthCodeDto;
import com.joojoo.api.user.presentation.dto.request.AuthTokenDto;
import com.joojoo.api.user.presentation.dto.request.UpdateProfileDto;
import com.joojoo.api.user.presentation.dto.response.DefaultProfileImageResponse;
import com.joojoo.api.user.presentation.dto.response.LoginResponse;
import com.joojoo.api.user.presentation.dto.response.UserInfoResponse;
import com.joojoo.global.common.request.auth.CustomUserDetails;
import com.joojoo.global.common.response.BaseResponse;
import com.joojoo.global.common.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "유저 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final AuthSocialService authSocialService;
    private final UserService userService;

    private final WithdrawUserUseCase withdrawUserUseCase;
    private final LogoutUseCase logoutUseCase;

    @Operation(summary = "카카오 로그인", description = "카카오 인가 코드를 전달받아 소셜 로그인을 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 인가 코드",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "502", description = "카카오 서버 통신 오류",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/kakao/login")
    public BaseResponse<LoginResponse> kakaoAppLogin(@RequestBody AuthTokenDto authTokenDto) {
        return BaseResponse.ok(authSocialService.kakaoAppSocialLogin(authTokenDto));
    }

    @Operation(summary = "애플 로그인", description = "애플 인가 코드를 전달받아 소셜 로그인을 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 인가 코드",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "502", description = "애플 서버 통신 오류",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/apple/login")
    public BaseResponse<LoginResponse> appleLogin(@RequestBody AuthCodeDto payload) {
        return BaseResponse.ok(authSocialService.appleSocialLogin(payload.getCode()));
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자를 탈퇴 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/withdraw")
    public BaseResponse<String> withdraw(@AuthenticationPrincipal CustomUserDetails user, HttpServletRequest request) {
        withdrawUserUseCase.withdraw(user.getUserId(), request);
        return BaseResponse.ok("회원 탈퇴가 완료되었습니다.");
    }

    @Operation(summary = "로그아웃", description = "현재 사용자의 리프레시 토큰을 만료시키고 로그아웃 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/logout")
    public BaseResponse<String> logout(@AuthenticationPrincipal CustomUserDetails user, HttpServletRequest request) {
        logoutUseCase.logout(user.getUserId(), request);
        return BaseResponse.ok("로그아웃이 완료되었습니다.");
    }

    @Operation(summary = "기본 프로필 API", description = "기본 프로필 이미지 리스트를 반홥합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 조회 성공"),
    })
    @GetMapping("/profile-images/default")
    public BaseResponse<DefaultProfileImageResponse> getDefaultProfileImages(){
        return BaseResponse.ok(userService.getDefaultProfileImages());
    }

    @Operation(summary = "프로필 업데이트 API", description = "기본 프로필 이미지 또는 이름을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 닉네임입니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PatchMapping("/profile")
    public BaseResponse<UserInfoResponse> updateProfile(@AuthenticationPrincipal CustomUserDetails user,
                                                        @RequestBody UpdateProfileDto updateProfileDto
    ){
        return BaseResponse.ok(userService.updateProfile(user.getUserId(), updateProfileDto));
    }

    @Operation(summary = "마이페이지 조회 API", description = "마이페이지에 이름 조회하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping
    public BaseResponse<UserInfoResponse> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails user
    ){
        return BaseResponse.ok(userService.getUserInfo(user.getUserId()));
    }

}
