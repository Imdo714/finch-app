package com.joojoo.api.ticker.presentation;

import com.joojoo.api.ticker.application.TickerService;
import com.joojoo.api.ticker.presentation.dto.response.TickerSearchResponse;
import com.joojoo.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TickerController {
    private final TickerService tickerService;

    // 테스트용 API: /add?name=삼성전자&ticker=005930
    @PostMapping("/add")
    public BaseResponse<String> addStock(@RequestParam String name, @RequestParam String ticker) {
        tickerService.addStockToRedis(name, ticker);
        return BaseResponse.ok(name + " (" + ticker + ") 저장 성공!");
    }

    @GetMapping("/search")
    public BaseResponse<TickerSearchResponse> searchStock(@RequestParam String query) {
        return BaseResponse.ok(tickerService.search(query));
    }
}
