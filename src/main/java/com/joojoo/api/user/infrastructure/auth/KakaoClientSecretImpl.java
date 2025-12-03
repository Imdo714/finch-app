package com.joojoo.api.user.infrastructure.auth;

import com.joojoo.api.user.domain.service.auth.KakaoClientSecret;
import com.joojoo.api.user.presentation.dto.request.kakao.AccessTokenDto;
import com.joojoo.api.user.presentation.dto.request.kakao.KakaoUserDto;
import com.joojoo.api.user.presentation.dto.request.kakao.KakaoUserResponse;
import com.joojoo.global.exception.handleException.auth.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class KakaoClientSecretImpl implements KakaoClientSecret {

    @Value("${KAKAO_CLIENT_ID}")
    private String client_id;

    @Value("${KAKAO_REDIRECT_URL}")
    private String redirect_url;

    @Override
    public AccessTokenDto getKakaoAccessToken(String code) {
        RestClient restClient = RestClient.create();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", client_id);
        params.add("redirect_uri", redirect_url);
        params.add("code", code);

        AccessTokenDto responseBody = restClient.post()
            .uri("https://kauth.kakao.com/oauth/token")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .body(params)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                log.error("[KakaoAuth] 토큰 발급 실패 (4xx) - 상태코드: {}, 내용: {}", response.getStatusCode(), new String(response.getBody().readAllBytes()));
                throw new InvalidAuthorizationException();
            })
            .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                log.error("[KakaoAuth] 토큰 발급 실패 (5xx) - 카카오 서버 오류: {}", response.getStatusCode());
                throw new KakaoTokenIssueFailedException();
            })
            .body(AccessTokenDto.class);

        if (responseBody == null || responseBody.getAccessToken() == null) {
            throw new KakaoInvalidTokenResponseException();
        }

        return responseBody;
    }

    @Override
    public KakaoUserDto getUserInfoFromKakao(String accessToken) {
        RestClient restClient = RestClient.create();

        KakaoUserResponse response = restClient.get()
            .uri("https://kapi.kakao.com/v2/user/me")
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, httpResponse) -> {
                log.error("[KakaoAuth] 유저 조회 실패 (4xx) - 상태코드: {}", httpResponse.getStatusCode());
                throw new InvalidAuthorizationException();
            })
            .onStatus(HttpStatusCode::is5xxServerError, (request, httpResponse) -> {
                log.error("[KakaoAuth] 유저 조회 실패 (5xx) - 카카오 서버 오류: {}", httpResponse.getStatusCode());
                throw new KakaoUserInfoRetrieveFailedException();
            })
            .body(KakaoUserResponse.class);

        if (response == null || response.getKakaoAccount() == null) {
            throw new KakaoInvalidUserResponseException();
        }

        return KakaoUserDto.of(response.getKakaoAccount(), response.getKakaoAccount().getProfile());
    }

}
