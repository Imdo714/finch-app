package com.joojoo.api.user.application;

import com.joojoo.api.jwt.application.JwtTokenUseCase;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.model.enums.DefaultProfileImage;
import com.joojoo.api.user.domain.provider.fileService;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.api.user.presentation.dto.request.UpdateProfileDto;
import com.joojoo.api.user.presentation.dto.response.DefaultProfileImageResponse;
import com.joojoo.api.user.presentation.dto.response.UserInfoResponse;
import com.joojoo.global.exception.handleException.users.UserNameDuplicatedException;
import com.joojoo.global.exception.handleException.users.UserNameRequiredException;
import com.joojoo.global.exception.handleException.users.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public UserInfoResponse updateProfile(Long userId, UpdateProfileDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        validateAndUpdateName(user, dto.getName()); // 이름 검증 및 업데이트
        updateOrInitProfileImage(user, dto.getProfile()); // 프로필 이미지 업데이트

        return UserInfoResponse.of(user, fileService.getFullUrl(user.getProfileImageUrl()));
    }

    private void validateAndUpdateName(User user, String newName) {
        if (newName != null && !newName.isBlank()) {
            if (!newName.equals(user.getName())) {
                validateDuplicateNickname(newName);
            }
            user.updateName(newName);
            return;
        }

        if (user.getName() == null) {
            throw new UserNameRequiredException();
        }
    }

    private void updateOrInitProfileImage(User user, DefaultProfileImage profileImage) {
        if (profileImage != null) {
            user.updateProfileImage(profileImage.getFileName());
            return;
        }

        if(user.getProfileImageUrl() == null){
            user.updateProfileImage(DefaultProfileImage.PROFILE_1.getFileName());
        }
    }

    private void validateDuplicateNickname(String nickname) {
        if (userRepository.existsByName(nickname)) {
            throw new UserNameDuplicatedException();
        }
    }

}
