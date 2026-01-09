package com.joojoo.api.user.application.port.out.social;

import com.joojoo.api.user.presentation.dto.request.apple.AppleTokenResponse;

public interface AppleWebClientSecret {
    String createClientSecret();

    AppleTokenResponse requestAppleToken(String code, String clientSecret);
}
