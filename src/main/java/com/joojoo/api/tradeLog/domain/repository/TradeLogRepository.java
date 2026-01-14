package com.joojoo.api.tradeLog.domain.repository;

import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.api.tradeLog.domain.service.calculate.TradeMetrics;
import com.joojoo.api.tradeLog.presentation.dto.response.chartOverlay.ChartOverlayResponse;

import java.util.List;

public interface TradeLogRepository {
    /** TradeLog 저장 */
    TradeLog save(TradeLog tradeLog);

    /** TradeLog 회원 삭제 (중간 테이블은 블럭에서 삭제해줌) */
    void withdrawByUserId(Long userId);

    /** 매매일지 조회 */
    TradeLog getTradeLogById(Long traderLogId);

    /** 매매일지에 사용한 Tags, Tickers 삭제 */
    void clearMetadataByTradeLog(Long userId, Long tradeLogId);

    /** 사용자가 특정 종목을 매수한 기록을 조회 */
    TradeMetrics findAllBuyLogsByTicker(Long userId, Long tickerId);

    /** 사용자가 특정 종목의 매수, 매도 거래 내역 조회 */
    List<ChartOverlayResponse.TradeDetailDto> getTradeBuySellRecords(Long userId, Long tickerId);

    /** TradeLog 조회 */
    TradeLog findById(Long tradeLogId);
}
