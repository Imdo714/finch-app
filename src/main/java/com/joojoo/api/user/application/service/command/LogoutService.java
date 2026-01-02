package com.joojoo.api.user.application.service.command;

import com.joojoo.api.jwt.application.JwtTokenUseCase;
import com.joojoo.api.user.application.port.in.LogoutUseCase;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LogoutService implements LogoutUseCase {

    private final UserRepository userRepository;
    private final JwtTokenUseCase jwtTokenUseCase;

    @Override
    public void logout(Long userId, HttpServletRequest request) {
        User user = userRepository.getUserById(userId);
        jwtTokenUseCase.clearUserTokens(userId, request);
    }


}
