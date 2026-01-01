package com.joojoo.api.user.application;

import com.joojoo.api.block.application.port.in.DeleteBlockUseCase;
import com.joojoo.api.jwt.application.JwtTokenUseCase;
import com.joojoo.api.tradeLog.application.port.in.DeleteTradeLogUseCase;
import com.joojoo.api.user.application.auth.withdraw.out.SocialUnlink;
import com.joojoo.api.user.application.port.in.GetUserUseCase;
import com.joojoo.api.user.application.port.in.LogoutUseCase;
import com.joojoo.api.user.application.port.in.WithdrawUserUseCase;
import com.joojoo.api.user.application.port.out.FilePort;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.model.enums.DefaultProfileImage;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.api.user.presentation.dto.response.DefaultProfileImageResponse;
import com.joojoo.api.user.presentation.dto.response.UserInfoResponse;
import com.joojoo.global.exception.handleException.users.UserNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserUseCaseService implements GetUserUseCase, WithdrawUserUseCase, LogoutUseCase {

    private final UserRepository userRepository;
    private final JwtTokenUseCase jwtTokenUseCase;
    private final DeleteBlockUseCase deleteBlockUseCase;
    private final DeleteTradeLogUseCase deleteTradeLogUseCase;
    private final Map<String, SocialUnlink> socialUnlink;
    private final FilePort filePort;

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

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
        User user = this.getUser(userId);
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

    @Override
    @Transactional
    public void withdraw(Long userId, HttpServletRequest request) {
        User user = this.getUser(userId);

        unSocialWithdraw(user); // 소셜 계정 탈퇴
        deleteBlockUseCase.deleteAllBlockMappings(userId); // 블럭(노트) 연관관계 정리
        deleteTradeLogUseCase.withdrawByUserId(userId); // 템플릿 정리
        jwtTokenUseCase.clearUserTokens(userId, request); // 토큰 정리
        user.withdraw(); // 회원 DB 정리

        // TODO : 추후에 고도화 이미지 삭제 해야 함 !!
    }

    private void unSocialWithdraw(User user) {
        SocialUnlink strategy = socialUnlink.get(user.getProvider().name());
        if (strategy != null) {
            strategy.unlink(user);
        }
    }

    @Override
    public void logout(Long userId, HttpServletRequest request) {
        User user = this.getUser(userId);
        jwtTokenUseCase.clearUserTokens(userId, request);
    }
}
