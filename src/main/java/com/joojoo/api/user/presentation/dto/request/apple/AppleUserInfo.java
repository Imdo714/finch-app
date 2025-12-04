package com.joojoo.api.user.presentation.dto.request.apple;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class AppleUserInfo {
    private String providerId;
    private String email;

    public static AppleUserInfo from(Map<String, Object> claims) {
        return new AppleUserInfo(
                (String) claims.get("sub"),
                (String) claims.get("email")
        );
    }
}
