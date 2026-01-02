package com.joojoo.api.user.domain.service;

import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.global.exception.handleException.users.UserNameDuplicatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    /** 이름이 변경되었다면 이름 증복 검사 */
    public void validateNicknameUpdate(User user, String newName) {
        if (newName == null || newName.equals(user.getName())) {
            return; // 변경사항 없으면 패스
        }

        if (userRepository.existsByName(newName)) {
            throw new UserNameDuplicatedException();
        }
    }

}
