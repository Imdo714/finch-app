package com.joojoo.api.user.infrastructure.social;

import com.joojoo.api.user.application.port.out.social.AppleClientSecret;
import com.joojoo.api.user.presentation.dto.request.apple.AppleTokenResponse;
import com.joojoo.api.user.presentation.dto.request.apple.AppleUserInfo;
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
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class AppleClientSecretImpl implements AppleClientSecret {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${APPLE_TEAM_ID}")
    private String teamId;

    @Value("${APPLE_KEY_ID}")
    private String keyId;

    @Value("${APPLE_CLIENT_ID}")
    private String clientId;

    @Value("${APPLE_PRIVATE_KEY}")
    private String privateKeyP8;

    @Override // Client Secret 생성
    public String createClientSecret() {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600000); // 1시간 유효

        return Jwts.builder()
                .setHeaderParam(JwsHeader.KEY_ID, keyId) // kid
                .setIssuer(teamId)                       // iss
                .setAudience("https://appleid.apple.com") // aud
                .setSubject(clientId)                    // sub
                .setIssuedAt(now)                        // iat
                .setExpiration(expiration)               // exp
                .signWith(getPrivateKey(), SignatureAlgorithm.ES256) // 서명
                .compact();
    }

    @Override // 애플 서버로 토큰 요청
    public AppleTokenResponse requestAppleToken(String code, String clientSecret) {
        RestClient restClient = RestClient.create();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("grant_type", "authorization_code");

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

    @Override // idToken 파싱
    public Map<String, Object> getAppleUserIdFromIdToken(String idToken) {
        try {
            String[] chunks = idToken.split("\\.");
            if (chunks.length < 2) {
                throw new IllegalArgumentException("Invalid Token Format");
            }

            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(payload, Map.class);

        } catch (Exception e) {
            log.error("[AppleAuth] 알 수 없는 오류 발생: {}", e.getMessage());
            throw new InvalidAuthorizationException();
        }
    }

    @Override
    public void sendRevokeRequest(String clientSecret, String socialRefreshToken) {
        RestClient restClient = RestClient.create();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("token", socialRefreshToken);
        params.add("token_type_hint", "refresh_token");

        restClient.post()
                .uri("https://appleid.apple.com/auth/revoke")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve() // 요청 전송 시작
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.warn("애플 연결 해제 실패 (4xx) - 이미 해제되었거나 유효하지 않음. 진행 계속함. 상태: {}", response.getStatusCode());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    log.error("애플 연결 해제 실패 (5xx) - 애플 서버 오류. 로컬 탈퇴 진행함. 상태: {}", response.getStatusCode());
                })
                .toBodilessEntity();
    }

    @Override
    public AppleUserInfo getAppleUserInfo(String idToken) {
        Map<String, Object> claims = this.getAppleUserIdFromIdToken(idToken);
        return AppleUserInfo.from(claims);
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
