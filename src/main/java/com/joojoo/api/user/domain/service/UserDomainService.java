package com.joojoo.api.user.domain.service;

import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.api.user.presentation.dto.request.kakao.KakaoUserDto;
import com.joojoo.api.util.port.in.GeneratorRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDomainService {

    private final UserRepository userRepository;
    private final GeneratorRandom generatorRandom;

    /** Kakao 로그인/회원가입 */
    public User registerOrLogin(KakaoUserDto kakaoUser, String socialRefreshToken) {
        return userRepository.findByProviderId(kakaoUser.getProviderId())
                .map(user -> {
                    user.updateSocialRefreshToken(socialRefreshToken);
                    return user;
                })
                .orElseGet(() -> {
                    User newUser = User.createKakaoUserBuilder(kakaoUser, socialRefreshToken);
                    return userRepository.save(newUser);
                });
    }

    /** Apple 로그인/회원가입 */
    public User registerOrLogin(String providerId, String appleRefreshToken, String email) {
        return userRepository.findByProviderId(providerId)
                .map(user -> {
                    if (appleRefreshToken != null) {
                        user.updateSocialRefreshToken(appleRefreshToken);
                    }
                    return user;
                })
                .orElseGet(() -> {
                    User newUser = User.createAppleUserBuilder(providerId, email, appleRefreshToken, generatorRandom.getRandomName());
                    return userRepository.save(newUser);
                });
    }
}
