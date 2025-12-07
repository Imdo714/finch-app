package com.joojoo.api.user.application;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    void logout(Long userId, HttpServletRequest request);
}
