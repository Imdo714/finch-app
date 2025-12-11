package com.joojoo.api.jwt.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenReissueDto {
    private String refreshToken;
}
