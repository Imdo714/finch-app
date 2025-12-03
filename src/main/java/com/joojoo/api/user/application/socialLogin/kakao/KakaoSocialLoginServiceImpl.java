package com.joojoo.api.user.application.socialLogin.kakao;

import com.joojoo.api.jwt.application.JwtTokenUseCase;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.api.user.domain.service.auth.KakaoClientSecret;
import com.joojoo.api.user.presentation.dto.request.kakao.AccessTokenDto;
import com.joojoo.api.user.presentation.dto.request.kakao.KakaoUserDto;
import com.joojoo.api.user.presentation.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class KakaoSocialLoginServiceImpl implements KakaoSocialLoginService {

    private final UserRepository userRepository;
    private final JwtTokenUseCase jwtTokenUseCase;
    private final KakaoClientSecret kakaoClientSecret;

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
}
