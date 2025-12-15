package com.joojoo.api.user.application;

import com.joojoo.api.user.presentation.dto.response.DefaultProfileImageResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    void logout(Long userId, HttpServletRequest request);

    DefaultProfileImageResponse getDefaultProfileImages();
}
