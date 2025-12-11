package com.joojoo.api.jwt.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReissueTokenResponse {
    private String accessToken;
}
