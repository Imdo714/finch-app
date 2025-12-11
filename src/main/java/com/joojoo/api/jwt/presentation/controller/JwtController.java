package com.joojoo.api.jwt.presentation.controller;

import com.joojoo.api.jwt.application.JwtTokenUseCase;
import com.joojoo.api.jwt.presentation.dto.request.TokenReissueDto;
import com.joojoo.api.jwt.presentation.dto.response.ReissueTokenResponse;
import com.joojoo.global.common.response.BaseResponse;
import com.joojoo.global.common.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "JWT API", description = "JWT 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class JwtController {

    private final JwtTokenUseCase jwtTokenUseCase;

    @Operation(summary = "AccessToken 재발급", description = "RefreshToken을 이용해 새로운 AccessToken을 받는다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "Refresh 토큰이 만료 되었습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "422", description = "유효하지 않은 토큰입니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/reissue")
    public BaseResponse<ReissueTokenResponse> refresh(@RequestBody TokenReissueDto tokenReissueDto) {
        return BaseResponse.ok(jwtTokenUseCase.reissueAccessToken(tokenReissueDto.getRefreshToken()));
    }
}
