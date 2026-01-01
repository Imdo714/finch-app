package com.joojoo.api.user.application.port.in;

import com.joojoo.api.user.domain.model.entity.User;

/** 회원을 조회하는 인터페이스 */
public interface GetUserUseCase {
    /** 회원 조회 */
    User getUser(Long userId);
}
