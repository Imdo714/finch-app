package com.joojoo.api.ticker.application;

import com.joojoo.api.ticker.presentation.dto.response.TickerSearchResponse;

public interface TickerService {
    void addStockToRedis(String name, String ticker);

    TickerSearchResponse search(String query);

    // 배포하지는 않고 로컬에서 csv 파일 실행 해 운영 DB에 데이터 넣기
    void initTickerData();
}
