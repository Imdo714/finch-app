package com.joojoo.api.user.infrastructure.persistence.social.withdraw;

import com.joojoo.api.user.application.port.out.social.SocialUnlink;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.application.port.out.social.AppleClientSecret;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("APPLE")
@RequiredArgsConstructor
public class AppleUnlink implements SocialUnlink {

    private final AppleClientSecret appleClientSecret;

    @Override
    public void unlink(User user) {
        if (user.getSocialRefresh() != null) {
            try {
                // 애플 클라이언트 아이디 생성하기
                String clientSecret = appleClientSecret.createClientSecret();

                // 애플 서버에 계정 탈퇴 요청
                appleClientSecret.sendRevokeRequest(clientSecret, user.getSocialRefresh());
            } catch (Exception e) {
                log.error("애플 연결 실패 계절 탈퇴 실패 : {}", e.getMessage());
            }
        }
    }

}
