package com.joojoo.api.tradeLog.presentation.controller;

import com.joojoo.api.tradeLog.application.port.in.CreateTradeLogUseCase;
import com.joojoo.api.tradeLog.presentation.dto.request.TradeRequestDto;
import com.joojoo.global.common.request.auth.CustomUserDetails;
import com.joojoo.global.common.response.BaseResponse;
import com.joojoo.global.common.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "TradeLog API", description = "템플릿 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/trades")
public class TradeLogController {

    private final CreateTradeLogUseCase createTradeLogUseCase;

    @Operation(summary = "템플릿 생성 API", description = "템플릿 작성 API입니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "템플릿 작성 성공",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "입력값 유효성 검증 실패 (필수 값 누락 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원, 티커를 찾을수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public BaseResponse<String> createTradesLog(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody TradeRequestDto tradeRequestDto
    ) {
        createTradeLogUseCase.createTradesLog(user.getUserId(), tradeRequestDto);
        return BaseResponse.ok("템플릿 작성 성공!");
    }

}
