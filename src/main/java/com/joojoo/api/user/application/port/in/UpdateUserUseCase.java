package com.joojoo.api.user.application.port.in;

import com.joojoo.api.user.presentation.dto.request.UpdateProfileDto;
import com.joojoo.api.user.presentation.dto.response.LoginResponse;
import com.joojoo.api.user.presentation.dto.response.UserInfoResponse;

public interface UpdateUserUseCase {

    /** 회원 이름, 프로필 업데이트 */
    UserInfoResponse updateProfile(Long userId, UpdateProfileDto updateProfileDto);

    /** 회원 동의 승인 시 권한 업데이트 */
    LoginResponse completeSignup(Long userId);
}
