package com.joojoo.api.user.application.service.command;

import com.joojoo.api.user.application.port.in.UpdateUserUseCase;
import com.joojoo.api.user.application.port.out.file.FilePort;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.api.user.domain.service.UserValidator;
import com.joojoo.api.user.presentation.dto.request.UpdateProfileDto;
import com.joojoo.api.user.presentation.dto.response.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserUpdateService implements UpdateUserUseCase  {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final FilePort filePort;

    @Override
    public UserInfoResponse updateProfile(Long userId, UpdateProfileDto dto) {
        User user = userRepository.getUserById(userId);

        userValidator.validateNicknameUpdate(user, dto.getName());

        String profileUrl = (dto.getProfile() != null) ? dto.getProfile().getFileName() : null;
        user.updateProfile(dto.getName(), profileUrl);

        return UserInfoResponse.of(user, filePort.getFullUrl(user.getProfileImageUrl()));
    }

    @Override
    public void completeSignup(Long userId) {
        User user = userRepository.getUserById(userId);
        user.activateUser();
    }

}
