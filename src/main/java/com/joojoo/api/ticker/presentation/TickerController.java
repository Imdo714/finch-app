package com.joojoo.api.ticker.presentation;

import com.joojoo.api.ticker.application.TickerService;
import com.joojoo.api.ticker.presentation.dto.response.TickerSearchResponse;
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
import org.springframework.web.bind.annotation.*;

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
}
