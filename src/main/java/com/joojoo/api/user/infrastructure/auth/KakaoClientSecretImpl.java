package com.joojoo.api.user.infrastructure.auth;

import com.joojoo.api.user.domain.service.auth.KakaoClientSecret;
import com.joojoo.api.user.presentation.dto.request.kakao.AccessTokenDto;
import com.joojoo.api.user.presentation.dto.request.kakao.KakaoUserDto;
import com.joojoo.api.user.presentation.dto.request.kakao.KakaoUserResponse;
import com.joojoo.global.common.exption.ExternalApiError;
import com.joojoo.global.exception.handelException.auth.SocialAuthException;
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
                    throw new SocialAuthException("유효하지 않은 인가 코드이거나 만료되었습니다.");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    log.error("[KakaoAuth] 토큰 발급 실패 (5xx) - 카카오 서버 오류: {}", response.getStatusCode());
                    throw new ExternalApiError("카카오 인증 서버에 일시적인 문제가 발생했습니다.");
                })
                .body(AccessTokenDto.class);

        if (responseBody == null || responseBody.getAccessToken() == null) {
            throw new ExternalApiError("카카오 토큰 발급 응답이 올바르지 않습니다.");
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
                .onStatus(HttpStatusCode::is4xxClientError, (request, res) -> {
                    log.error("[KakaoAuth] 유저 조회 실패 (4xx) - 상태코드: {}", res.getStatusCode());
                    throw new SocialAuthException("카카오 액세스 토큰이 유효하지 않습니다.");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, res) -> {
                    log.error("[KakaoAuth] 유저 조회 실패 (5xx) - 카카오 서버 오류: {}", res.getStatusCode());
                    throw new ExternalApiError("카카오 유저 정보 조회 서버에 문제가 발생했습니다.");
                })
                .body(KakaoUserResponse.class);

        if (response == null || response.getKakaoAccount() == null) {
            throw new ExternalApiError("카카오 유저 정보를 불러오는데 실패했습니다.");
        }

        return KakaoUserDto.of(response.getKakaoAccount(), response.getKakaoAccount().getProfile());
    }

}
