package com.joojoo.api.user.application.service.query;

import com.joojoo.api.user.application.port.in.GetUserUseCase;
import com.joojoo.api.user.application.port.out.file.FilePort;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.model.enums.DefaultProfileImage;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.api.user.presentation.dto.response.DefaultProfileImageResponse;
import com.joojoo.api.user.presentation.dto.response.UserInfoResponse;
import com.joojoo.global.exception.handleException.users.UserNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserQueryService implements GetUserUseCase {

    private final UserRepository userRepository;
    private final FilePort filePort;

    @Override
    public User getUserReference(Long userId) {
        try {
            return userRepository.getReferenceById(userId);
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundException();
        }
    }

    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.getUserById(userId);
        return UserInfoResponse.of(user);
    }

    @Override
    public DefaultProfileImageResponse getDefaultProfileImages() {
        return DefaultProfileImageResponse.of(Arrays.stream(DefaultProfileImage.values())
                .map(img -> new DefaultProfileImageResponse.ProfileImage(
                        img.name(),
                        filePort.getFullUrl(img.getFileName()),
                        img.getDescription()
                ))
                .collect(Collectors.toList())
        );
    }

}
