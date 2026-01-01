package com.joojoo.api.user.application.auth;

import com.joojoo.api.jwt.application.JwtTokenUseCase;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.api.user.domain.service.auth.AppleClientSecret;
import com.joojoo.api.user.domain.service.auth.KakaoClientSecret;
import com.joojoo.api.user.presentation.dto.request.AuthTokenDto;
import com.joojoo.api.user.presentation.dto.request.apple.AppleTokenResponse;
import com.joojoo.api.user.presentation.dto.request.apple.AppleUserInfo;
import com.joojoo.api.user.presentation.dto.request.kakao.AccessTokenDto;
import com.joojoo.api.user.presentation.dto.request.kakao.KakaoUserDto;
import com.joojoo.api.user.presentation.dto.response.LoginResponse;
import com.joojoo.api.util.random.GeneratorRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthSocialServiceImpl implements AuthSocialService {

    private final UserRepository userRepository;
    private final JwtTokenUseCase jwtTokenUseCase;
    private final KakaoClientSecret kakaoClientSecret;
    private final AppleClientSecret appleClientSecret;
    private final GeneratorRandom generatorRandom;

    @Override
    @Transactional
    public LoginResponse kakaoWebSocialLogin(String code) {
        AccessTokenDto kakaoAccessToken = kakaoClientSecret.getKakaoAccessToken(code);
        KakaoUserDto userInfo = kakaoClientSecret.getUserInfoFromKakao(kakaoAccessToken.getAccessToken());

        User user = registerOrLogin(userInfo, kakaoAccessToken.getRefreshToken());
        return generateLoginResponse(user);
    }

    @Override
    public LoginResponse kakaoAppSocialLogin(AuthTokenDto authTokenDto) {
        KakaoUserDto userInfo = kakaoClientSecret.getUserInfoFromKakao(authTokenDto.getAccessToken());

        User user = registerOrLogin(userInfo, authTokenDto.getRefreshToken());
        return generateLoginResponse(user);
    }

    @Override
    @Transactional
    public LoginResponse appleSocialLogin(String code) {
        String clientSecret = appleClientSecret.createClientSecret();
        AppleTokenResponse appleTokenResponse = appleClientSecret.requestAppleToken(code, clientSecret);
        AppleUserInfo appleUser = getAppleUserInfo(appleTokenResponse.getIdToken());

        User user = registerOrLogin(appleUser.getProviderId(), appleTokenResponse.getRefreshToken(), appleUser.getEmail());
        return generateLoginResponse(user);
    }

    private LoginResponse generateLoginResponse(User user){
        String refreshToken = jwtTokenUseCase.createAndSaveRefreshToken(user.getId(), user.getName(), user);
        String accessToken = jwtTokenUseCase.createAccessToken(user.getId(), user.getName());
        return LoginResponse.of(user, accessToken, refreshToken);
    }

    private User registerOrLogin(KakaoUserDto kakaoUser, String socialRefreshToken) {
        return userRepository.findByEmail(kakaoUser.getEmail())
            .map(user -> {
                user.updateSocialRefreshToken(socialRefreshToken);
                return user;
            })
            .orElseGet(() -> {
                User newUser = User.createKakaoUserBuilder(kakaoUser, socialRefreshToken);
                return userRepository.save(newUser);
            });
    }

    // idToken으로 사용자 정보 추출하는 메서드
    public AppleUserInfo getAppleUserInfo(String idToken) {
        Map<String, Object> claims = appleClientSecret.getAppleUserIdFromIdToken(idToken);
        return AppleUserInfo.from(claims);
    }

    private User registerOrLogin(String providerId, String appleRefreshToken, String email) {
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
