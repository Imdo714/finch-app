package com.joojoo.api.user.application.auth;

import com.joojoo.api.jwt.application.JwtTokenUseCase;
import com.joojoo.api.user.application.auth.withdraw.out.SocialUnlink;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.api.user.domain.service.auth.AppleClientSecret;
import com.joojoo.api.user.domain.service.auth.KakaoClientSecret;
import com.joojoo.api.user.presentation.dto.request.apple.AppleTokenResponse;
import com.joojoo.api.user.presentation.dto.request.apple.AppleUserInfo;
import com.joojoo.api.user.presentation.dto.request.kakao.AccessTokenDto;
import com.joojoo.api.user.presentation.dto.request.kakao.KakaoUserDto;
import com.joojoo.api.user.presentation.dto.response.LoginResponse;
import com.joojoo.global.exception.handleException.auth.InvalidAuthorizationException;
import com.joojoo.global.exception.handleException.users.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
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
    private final Map<String, SocialUnlink> socialUnlink;

    @Override
    @Transactional
    public LoginResponse kakaoSocialLogin(String code) {
        AccessTokenDto kakaoAccessToken = kakaoClientSecret.getKakaoAccessToken(code);
        KakaoUserDto userInfo = kakaoClientSecret.getUserInfoFromKakao(kakaoAccessToken.getAccessToken());

        User user = registerOrLogin(userInfo, kakaoAccessToken.getRefreshToken());
        String refreshToken = jwtTokenUseCase.createAndSaveRefreshToken(user.getId(), user.getName(), user);
        String accessToken = jwtTokenUseCase.createAccessToken(user.getId(), user.getName());

        return LoginResponse.of(user, accessToken, refreshToken);
    }

    @Override
    @Transactional
    public LoginResponse appleSocialLogin(String code) {
        String clientSecret = appleClientSecret.createClientSecret();
        AppleTokenResponse appleTokenResponse = appleClientSecret.requestAppleToken(code, clientSecret);
        log.info("Apple Token Response: {}", appleTokenResponse);

        AppleUserInfo appleUser = getAppleUserInfo(appleTokenResponse.getIdToken());
        log.info("Apple User ID: {}", appleUser.getProviderId());
        log.info("Apple User Email: {}", appleUser.getEmail());

        User user = registerOrLogin(appleUser.getProviderId(), appleTokenResponse, appleUser.getEmail());
        String refreshToken = jwtTokenUseCase.createAndSaveRefreshToken(user.getId(), user.getName(), user);
        String accessToken = jwtTokenUseCase.createAccessToken(user.getId(), user.getName());

        return LoginResponse.of(user, accessToken, refreshToken);
    }

    @Override
    @Transactional
    public void withdraw(Long userId, HttpServletRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        unSocialWithdraw(user); // 소셜 계정 탈퇴
        jwtTokenUseCase.clearUserTokens(userId, request); // 토큰 정리
        user.withdraw(); // 회원 DB 정리

        // TODO : 추후에 다른 테이블도 삭제 해야 함 !!
    }

    private void unSocialWithdraw(User user) {
        SocialUnlink strategy = socialUnlink.get(user.getProvider().name());
        if (strategy != null) {
            strategy.unlink(user);
        }
    }

    private User registerOrLogin(KakaoUserDto kakaoUser, String socialRefreshToken) {
        return userRepository.findByEmail(kakaoUser.getEmail())
                .map(user -> {
                    // user가 있으면 RefreshToken 업데이트
                    user.updateSocialRefreshToken(socialRefreshToken);
                    return userRepository.save(user);
                })
                .orElseGet(() -> {
                    // user가 없다면 DB에 저장
                    User newUser = User.createKakaoUserBuilder(kakaoUser, socialRefreshToken);
                    return userRepository.save(newUser);
                });
    }

    // idToken으로 사용자 정보 추출하는 메서드
    public AppleUserInfo getAppleUserInfo(String idToken) {
        Map<String, Object> claims = appleClientSecret.getAppleUserIdFromIdToken(idToken);
        return AppleUserInfo.from(claims);
    }

    private User registerOrLogin(String providerId, AppleTokenResponse appleTokenResponse, String email) {
        return userRepository.findByProviderId(providerId)
                .map(user -> {
                    if (appleTokenResponse.getRefreshToken() != null) {
                        user.updateSocialRefreshToken(appleTokenResponse.getRefreshToken());
                    }
                    return user;
                })
                .orElseGet(() -> {
                    User newUser = User.createAppleUserBuilder(providerId, email, appleTokenResponse.getRefreshToken());
                    return userRepository.save(newUser);
                });
    }
}
