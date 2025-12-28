package com.joojoo.api.ticker.presentation.controller;

import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagsResponse;
import com.joojoo.api.ticker.application.TickerService;
import com.joojoo.api.ticker.presentation.dto.response.TickerSearchResponse;
import com.joojoo.global.common.request.auth.CustomUserDetails;
import com.joojoo.global.common.response.BaseResponse;
import com.joojoo.global.common.response.ErrorResponse;
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
public class TickerController {
    private final TickerService tickerService;

    @Operation(summary = "주식 티커 추가 (검색 확인용)", description = "Redis에 새로운 주식 이름과 티커 심볼을 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "저장 성공"),
            @ApiResponse(responseCode = "401", description = "티커 또는 이름을 작성하세요.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/add") // 테스트용 API: /add?name=삼성전자&ticker=005930
    public BaseResponse<String> addStock(
            @Parameter(description = "주식 종목명 (예: 삼성전자)", required = true)
            @RequestParam String name,
            @Parameter(description = "주식 티커 (예: 005930)", required = true)
            @RequestParam String ticker
    ) {
        tickerService.addStockToRedis(name, ticker);
        return BaseResponse.ok(name + " (" + ticker + ") 저장 성공!");
    }

    @Operation(summary = "티커 검색", description = "쿼리를 통해 저장된 주식 티커를 검색합니다.")
    @GetMapping("/search")
    public BaseResponse<TickerSearchResponse> searchStock(
            @Parameter(description = "검색어 (종목명)", required = true)
            @RequestParam String query
    ) {
        return BaseResponse.ok(tickerService.search(query));
    }

    @PostMapping("/load-Cache")
    public void dbToRedis(@AuthenticationPrincipal CustomUserDetails user){
        tickerService.loadTickersToCache(user.getUserId());
    }

    @Operation(summary = "티커 상세 API", description = "특정 티커를 조회하여 Block, TradeLog에 작성한 티커를 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/{tickerId}")
    public BaseResponse<BlockTagsResponse> getTickerList(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long tickerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastDate
    ){
        return BaseResponse.ok(tickerService.getTickerList(user.getUserId(), tickerId, lastDate));
    }
}
