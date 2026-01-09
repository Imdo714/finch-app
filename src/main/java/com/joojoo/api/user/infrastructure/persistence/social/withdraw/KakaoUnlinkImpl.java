package com.joojoo.api.user.infrastructure.persistence.social.withdraw;

import com.joojoo.api.user.application.port.out.social.SocialUnlink;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.model.enums.Provider;
import com.joojoo.api.user.application.port.out.social.KakaoClientSecret;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("KAKAO")
@RequiredArgsConstructor
public class KakaoUnlinkImpl implements SocialUnlink {

    private final KakaoClientSecret kakaoClientSecret;

    @Override
    public void unlink(User user) {
        if (user.getProvider() == Provider.KAKAO && user.getSocialRefresh() != null) {
            try {
                // 카카오 새로운 AccessToken 받아오기
                String kakaoAccessToken = kakaoClientSecret.renewKakaoAccessToken(user.getSocialRefresh());

                // 카카오 서버에 계정 탈퇴 요청
                kakaoClientSecret.unlinkKakaoUser(kakaoAccessToken);
            } catch (Exception e) {
                log.error("카카오 연결 끊기 실패 (이미 끊겼거나 토큰 만료): ", e);
            }
        }
    }

}
