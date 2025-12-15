package com.joojoo.api.user.application;

import com.joojoo.api.jwt.application.JwtTokenUseCase;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.model.enums.DefaultProfileImage;
import com.joojoo.api.user.domain.provider.fileService;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.api.user.presentation.dto.response.DefaultProfileImageResponse;
import com.joojoo.global.exception.handleException.users.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtTokenUseCase jwtTokenUseCase;
    private final fileService fileService;

    @Override
    public void logout(Long userId, HttpServletRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        // 토큰 정리
        jwtTokenUseCase.clearUserTokens(userId, request);
    }

    @Override
    public DefaultProfileImageResponse getDefaultProfileImages() {
        return DefaultProfileImageResponse.of(Arrays.stream(DefaultProfileImage.values())
                .map(img -> new DefaultProfileImageResponse.ProfileImage(
                        img.name(),
                        fileService.getFullUrl(img.getFileName()),
                        img.getDescription()
                ))
                .collect(Collectors.toList())
        );
    }

}
