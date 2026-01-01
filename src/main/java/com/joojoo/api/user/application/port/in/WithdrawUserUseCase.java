package com.joojoo.api.user.application.port.in;

import jakarta.servlet.http.HttpServletRequest;

/** 계정을 탈퇴하는 인터페이스 */
public interface WithdrawUserUseCase {
    void withdraw(Long userId, HttpServletRequest request);
}
