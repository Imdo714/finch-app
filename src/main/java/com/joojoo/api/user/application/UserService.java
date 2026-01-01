package com.joojoo.api.user.application;

import com.joojoo.api.user.presentation.dto.request.UpdateProfileDto;
import com.joojoo.api.user.presentation.dto.response.DefaultProfileImageResponse;
import com.joojoo.api.user.presentation.dto.response.UserInfoResponse;

public interface UserService {

    DefaultProfileImageResponse getDefaultProfileImages();

    UserInfoResponse updateProfile(Long userId, UpdateProfileDto updateProfileDto);

    UserInfoResponse getUserInfo(Long userId);
}
