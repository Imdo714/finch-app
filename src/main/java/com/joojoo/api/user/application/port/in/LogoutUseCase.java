package com.joojoo.api.user.application.port.in;

import jakarta.servlet.http.HttpServletRequest;

public interface LogoutUseCase {
    void logout(Long userId, HttpServletRequest request);
}
