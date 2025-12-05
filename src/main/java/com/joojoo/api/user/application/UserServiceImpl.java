package com.joojoo.api.user.application;

import com.joojoo.api.jwt.application.JwtTokenUseCase;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.global.exception.handleException.users.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtTokenUseCase jwtTokenUseCase;

    @Override
    public void logout(Long userId, HttpServletRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        // 토큰 정리
        jwtTokenUseCase.clearUserTokens(userId, request);
    }



}
