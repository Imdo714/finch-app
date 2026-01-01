package com.joojoo.api.user.application;

import com.joojoo.api.user.presentation.dto.request.UpdateProfileDto;
import com.joojoo.api.user.presentation.dto.response.UserInfoResponse;

public interface UserService {
    UserInfoResponse updateProfile(Long userId, UpdateProfileDto updateProfileDto);
}
