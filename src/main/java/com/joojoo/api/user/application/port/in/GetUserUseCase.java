package com.joojoo.api.user.application.port.in;

import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.presentation.dto.response.DefaultProfileImageResponse;
import com.joojoo.api.user.presentation.dto.response.UserInfoResponse;

/** 회원을 조회하는 인터페이스 */
public interface GetUserUseCase {
    /** 회원 조회 */
    User getUser(Long userId);

    /** 회원 프록시 객체 */
    User getUserReference(Long userId);

    /** 마이페이지 회원 정보 조회 */
    UserInfoResponse getUserInfo(Long userId);

    /** 회원 기본프로필 조회 */
    DefaultProfileImageResponse getDefaultProfileImages();
}
