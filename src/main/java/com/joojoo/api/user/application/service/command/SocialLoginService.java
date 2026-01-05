package com.joojoo.api.user.application.service.command;

import com.joojoo.api.jwt.application.JwtTokenUseCase;
import com.joojoo.api.user.application.port.in.SocialLoginUseCase;
import com.joojoo.api.user.application.port.out.social.AppleClientSecret;
import com.joojoo.api.user.application.port.out.social.AppleWeb;
import com.joojoo.api.user.application.port.out.social.KakaoClientSecret;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.service.UserDomainService;
import com.joojoo.api.user.presentation.dto.request.AuthTokenDto;
import com.joojoo.api.user.presentation.dto.request.apple.AppleTokenResponse;
import com.joojoo.api.user.presentation.dto.request.apple.AppleUserInfo;
import com.joojoo.api.user.presentation.dto.request.kakao.AccessTokenDto;
import com.joojoo.api.user.presentation.dto.request.kakao.KakaoUserDto;
import com.joojoo.api.user.presentation.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SocialLoginService implements SocialLoginUseCase {

    private final JwtTokenUseCase jwtTokenUseCase;
    private final UserDomainService userDomainService;
    private final KakaoClientSecret kakaoClientSecret;
    private final AppleClientSecret appleClientSecret;

    @Override
    @Transactional
    public LoginResponse kakaoWebSocialLogin(String code) {
        AccessTokenDto kakaoAccessToken = kakaoClientSecret.getKakaoAccessToken(code);
        KakaoUserDto userInfo = kakaoClientSecret.getUserInfoFromKakao(kakaoAccessToken.getAccessToken());

        User user = userDomainService.registerOrLogin(userInfo, kakaoAccessToken.getRefreshToken());
        return generateLoginResponse(user);
    }

    @Override
    public LoginResponse kakaoAppSocialLogin(AuthTokenDto authTokenDto) {
        KakaoUserDto userInfo = kakaoClientSecret.getUserInfoFromKakao(authTokenDto.getAccessToken());

        User user = userDomainService.registerOrLogin(userInfo, authTokenDto.getRefreshToken());
        return generateLoginResponse(user);
    }

    @Override
    @Transactional
    public LoginResponse appleSocialLogin(String code) {
        String clientSecret = appleClientSecret.createClientSecret();
        AppleTokenResponse appleTokenResponse = appleClientSecret.requestAppleToken(code, clientSecret);
        AppleUserInfo appleUser = appleClientSecret.getAppleUserInfo(appleTokenResponse.getIdToken());

        User user = userDomainService.registerOrLogin(appleUser.getProviderId(), appleTokenResponse.getRefreshToken(), appleUser.getEmail());
        return generateLoginResponse(user);
    }

    private final AppleWeb appleWeb;

    @Override
    public LoginResponse appleWebLogin(String code) {
        // Client Secret 생성
        String clientSecret = appleWeb.createClientSecret();

        // 애플 토큰 요청 (redirectUri 포함)
        AppleTokenResponse appleTokenResponse = appleWeb.requestAppleToken(code, clientSecret);
        AppleUserInfo appleUser = appleClientSecret.getAppleUserInfo(appleTokenResponse.getIdToken());

        User user = userDomainService.registerOrLogin(appleUser.getProviderId(), appleTokenResponse.getRefreshToken(), appleUser.getEmail());
        return generateLoginResponse(user);
    }

    @Override
    public String getRedirectUrl(String code) {
        StringBuilder uriBuilder = new StringBuilder("intent://callback");
        uriBuilder.append("?code=").append(code);
        uriBuilder.append("#Intent;");
        uriBuilder.append("package=").append(appleClientSecret.getPackageName()).append(";");
        uriBuilder.append("scheme=").append(appleClientSecret.getScheme()).append(";");
        uriBuilder.append("end");

        return uriBuilder.toString();
    }

    private LoginResponse generateLoginResponse(User user){
        String refreshToken = jwtTokenUseCase.createAndSaveRefreshToken(user.getId(), user.getName(), user);
        String accessToken = jwtTokenUseCase.createAccessToken(user.getId(), user.getName());
        return LoginResponse.of(user, accessToken, refreshToken);
    }

}
