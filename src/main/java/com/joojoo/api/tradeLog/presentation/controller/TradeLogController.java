package com.joojoo.api.tradeLog.presentation.controller;

import com.joojoo.api.common.domain.request.auth.CustomUserDetails;
import com.joojoo.api.common.domain.response.BaseResponse;
import com.joojoo.api.common.domain.response.ErrorResponse;
import com.joojoo.api.tradeLog.application.port.in.CreateTradeLogUseCase;
import com.joojoo.api.tradeLog.application.port.in.DeleteTradeLogUseCase;
import com.joojoo.api.tradeLog.application.port.in.GetTraderLogUseCase;
import com.joojoo.api.tradeLog.presentation.dto.request.TradeRequestDto;
import com.joojoo.api.tradeLog.presentation.dto.request.calculate.TradeCalculateRequest;
import com.joojoo.api.tradeLog.presentation.dto.response.calculate.TradeMetricsResponse;
import com.joojoo.api.tradeLog.presentation.dto.response.chartOverlay.ChartOverlayResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "TradeLog API", description = "템플릿 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/trades")
public class TradeLogController {

    private final GetTraderLogUseCase getTraderLogUseCase;
    private final CreateTradeLogUseCase createTradeLogUseCase;
    private final DeleteTradeLogUseCase deleteTradeLogUseCase;

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

    @Operation(summary = "템플릿 삭제 API", description = "템플릿 삭제 API입니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "템플릿 삭제 성공",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 매매일지를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{tradeLogId}")
    public BaseResponse<String> deleteBlock(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long tradeLogId
    ) {
        deleteTradeLogUseCase.deleteTradeLog(user.getUserId(), tradeLogId);
        return BaseResponse.ok("템플릿 삭제 성공!");
    }

    @Operation(summary = "템플릿 거래 정보 API", description = "템플릿 거래 정보 API입니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = TradeMetricsResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 매매일지를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/calculate")
    public BaseResponse<TradeMetricsResponse> getTradeLogCalculate(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody TradeCalculateRequest tradeCalculateRequest
            ) {
        return BaseResponse.ok(getTraderLogUseCase.getTradeLogCalculate(user.getUserId(), tradeCalculateRequest));
    }

    @Operation(summary = "차트 위 포인트 내역 API", description = "차트 위 포인트 내역 API입니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ChartOverlayResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 매매일지를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/chart/{tickerId}")
    public BaseResponse<ChartOverlayResponse> getTradeLogChart(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long tickerId
    ) {
        return BaseResponse.ok(getTraderLogUseCase.getTradeLogChart(user.getUserId(), tickerId));
    }

}
