package com.joojoo.api.user.application.port.out.social;

import com.joojoo.api.user.presentation.dto.request.apple.AppleTokenResponse;
import com.joojoo.global.exception.handleException.auth.InvalidAuthorizationException;
import com.joojoo.global.exception.handleException.auth.apple.AppleInvalidTokenResponseException;
import com.joojoo.global.exception.handleException.auth.apple.AppleTokenIssueFailedException;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class AppleWeb {

    @Value("${APPLE_TEAM_ID}")
    private String teamId;

    @Value("${APPLE_KEY_ID}")
    private String keyId;

    @Value("${APPLE_SERVICE_ID}")
    private String clientServiceId;

    @Value("${APPLE_REDIRECT_URL}")
    private String redirectUri;

    @Value("${APPLE_PRIVATE_KEY}")
    private String privateKeyP8;

    // Client Secret 생성
    public String createClientSecret() {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600000); // 1시간 유효

        return Jwts.builder()
                .setHeaderParam(JwsHeader.KEY_ID, keyId) // kid
                .setIssuer(teamId)                       // iss
                .setAudience("https://appleid.apple.com") // aud
                .setSubject(clientServiceId)                    // sub
                .setIssuedAt(now)                        // iat
                .setExpiration(expiration)               // exp
                .signWith(getPrivateKey(), SignatureAlgorithm.ES256) // 서명
                .compact();
    }


    public AppleTokenResponse requestAppleToken(String code, String clientSecret) {
        RestClient restClient = RestClient.create();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientServiceId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("grant_type", "authorization_code");

        // ★ 웹/안드로이드의 경우 필수 추가
        params.add("redirect_uri", redirectUri);

        AppleTokenResponse responseBody = restClient.post()
                .uri("https://appleid.apple.com/auth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.error("[AppleAuth] 토큰 발급 실패 (4xx) - 상태코드: {}, 내용: {}", response.getStatusCode(), new String(response.getBody().readAllBytes()));
                    throw new InvalidAuthorizationException();
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    log.error("[AppleAuth] 토큰 발급 실패 (5xx) - 애플 서버 오류: {}", response.getStatusCode());
                    throw new AppleTokenIssueFailedException();
                })
                .body(AppleTokenResponse.class);

        if (responseBody == null || responseBody.getAccessToken() == null) {
            throw new AppleInvalidTokenResponseException();
        }

        return responseBody;
    }


    // PrivateKey 객체 생성 헬퍼 (BouncyCastle 라이브러리 필요할 수 있음)
    private PrivateKey getPrivateKey() {
        try {
            // p8 파일의 "-----BEGIN PRIVATE KEY-----" 등을 제거하고 내용만 가져와야 함
            String privateKeyContent = privateKeyP8
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] encoded = Base64.getDecoder().decode(privateKeyContent);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance("EC");
            return kf.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Private Key 생성 실패", e);
        }
    }
}
