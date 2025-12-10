package com.joojoo.api.user.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthTokenDto {
    private String accessToken;
    private String refreshToken;
}
