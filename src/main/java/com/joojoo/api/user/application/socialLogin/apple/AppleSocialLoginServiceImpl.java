package com.joojoo.api.user.application.socialLogin.apple;

import com.joojoo.api.jwt.application.JwtTokenUseCase;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.api.user.domain.service.auth.AppleClientSecret;
import com.joojoo.api.user.presentation.dto.request.apple.AppleTokenResponse;
import com.joojoo.api.user.presentation.dto.request.apple.AppleUserInfo;
import com.joojoo.api.user.presentation.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleSocialLoginServiceImpl implements AppleSocialLoginService {

    private final UserRepository userRepository;
    private final JwtTokenUseCase jwtTokenUseCase;
    private final AppleClientSecret appleClientSecret;

    @Override
    public LoginResponse appleSocialLogin(String authorizationCode) {
        String clientSecret = appleClientSecret.createClientSecret();
        AppleTokenResponse appleTokenResponse = appleClientSecret.requestAppleToken(authorizationCode, clientSecret);
        log.info("Apple Token Response: {}", appleTokenResponse);

        AppleUserInfo appleUser = getAppleUserInfo(appleTokenResponse.getIdToken());
        log.info("Apple User ID: {}", appleUser.getProviderId());
        log.info("Apple User Email: {}", appleUser.getEmail());

        User user = registerOrLogin(appleUser.getProviderId(), appleTokenResponse, appleUser.getEmail());
        String refreshToken = jwtTokenUseCase.createAndSaveRefreshToken(user.getId(), user.getName(), user);
        String accessToken = jwtTokenUseCase.createAccessToken(user.getId(), user.getName());

        return LoginResponse.of(user, accessToken, refreshToken);
    }

    // idToken으로 사용자 정보 추출하는 메서드
    public AppleUserInfo getAppleUserInfo(String idToken) {
        Map<String, Object> claims = appleClientSecret.getAppleUserIdFromIdToken(idToken);
        return AppleUserInfo.from(claims);
    }

    private User registerOrLogin(String providerId, AppleTokenResponse appleTokenResponse, String email) {
        return userRepository.findByProviderId(providerId)
                .map(user -> {
                    log.info("Apple Login !!!");
                    if (appleTokenResponse.getRefreshToken() != null) {
                        user.updateSocialRefreshToken(appleTokenResponse.getRefreshToken());
                    }
                    return user;
                })
                .orElseGet(() -> {
                    log.info("Apple register !!!");
                    User newUser = User.createAppleUserBuilder(providerId, email, appleTokenResponse.getRefreshToken());
                    return userRepository.save(newUser);
                });
    }

}
