package com.joojoo.api.ticker.application;

import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagsResponse;
import com.joojoo.api.blockTag.presentation.dto.response.detail.TotalCountResponse;
import com.joojoo.api.ticker.presentation.dto.response.TickerSearchResponse;

import java.time.LocalDate;

public interface TickerService {
    void addStockToRedis(String name, String ticker);

    TickerSearchResponse search(String query);

    // 배포하지는 않고 로컬에서 csv 파일 실행 해 운영 DB에 데이터 넣기
    void initTickerData();

    // 운영 DB에 있는 주식을 Redis에 저장
    void loadTickersToCache(Long userId);

    BlockTagsResponse getTickerList(Long userId, Long tickerId, LocalDate lastDate);

    /** 티커 상세페이지 총 개수 */
    TotalCountResponse getTickerDetailCount(Long userId, Long tickerId);
}
