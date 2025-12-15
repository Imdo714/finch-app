package com.joojoo.api.user.application;

import com.joojoo.api.user.presentation.dto.request.UpdateProfileDto;
import com.joojoo.api.user.presentation.dto.response.DefaultProfileImageResponse;
import com.joojoo.api.user.presentation.dto.response.UserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    void logout(Long userId, HttpServletRequest request);

    DefaultProfileImageResponse getDefaultProfileImages();

    UserInfoResponse updateProfile(Long userId, UpdateProfileDto updateProfileDto);
}
