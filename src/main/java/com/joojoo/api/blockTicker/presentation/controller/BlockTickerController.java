package com.joojoo.api.blockTicker.presentation.controller;

import com.joojoo.api.blockTicker.application.port.in.GetTickerUseCase;
import com.joojoo.api.blockTicker.presentation.dto.response.recent.RecentTickersResponse;
import com.joojoo.api.common.domain.request.auth.CustomUserDetails;
import com.joojoo.api.common.domain.response.BaseResponse;
import com.joojoo.api.common.domain.response.ErrorResponse;
import com.joojoo.api.common.domain.response.detail.DailyBlockDetailsResponse;
import com.joojoo.api.common.domain.response.detail.count.TotalCountResponse;
import com.joojoo.api.ticker.presentation.dto.response.TickerSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ticker")
@Tag(name = "Ticker API", description = "주식 티커 관련 API")
public class BlockTickerController {

    private final GetTickerUseCase getTickerUseCase;

    @Operation(summary = "티커 검색", description = "쿼리를 통해 저장된 주식 티커를 검색합니다.")
    @GetMapping("/search")
    public BaseResponse<TickerSearchResponse> searchStock(
            @Parameter(description = "검색어 (종목명)", required = true)
            @RequestParam String query
    ) {
        return BaseResponse.ok(getTickerUseCase.search(query));
    }

    @Operation(summary = "티커 상세 API", description = "특정 티커를 조회하여 Block, TradeLog에 작성한 티커를 가져옵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = DailyBlockDetailsResponse.class)
                    )
            )
    })
    @GetMapping("/{tickerId}")
    public BaseResponse<DailyBlockDetailsResponse> getTickerDetail(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long tickerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastDate
    ){
        return BaseResponse.ok(getTickerUseCase.getTickerDetail(user.getUserId(), tickerId, lastDate));
    }

    @Operation(summary = "Ticker 상세 페이지 블럭 개수 API", description = "티커 상세 페이지 위에 티커 이름 하고 수량을 조회하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = TotalCountResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "티커를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{tickerId}/count")
    public BaseResponse<TotalCountResponse> getTickerDetailCount(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long tickerId
    ){
        return BaseResponse.ok(getTickerUseCase.getTickerDetailCount(user.getUserId(), tickerId));
    }

    @Operation(summary = "Ticker 최근 사용 기록", description = "Ticker 작성할때 최근 10개 기록 리스트 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = RecentTickersResponse.class)
                    )
            )
    })
    @GetMapping("/recent")
    public BaseResponse<RecentTickersResponse> getRecentTickers(
            @AuthenticationPrincipal CustomUserDetails user
    ){
        return BaseResponse.ok(getTickerUseCase.getRecentTickers(user.getUserId()));
    }

}
