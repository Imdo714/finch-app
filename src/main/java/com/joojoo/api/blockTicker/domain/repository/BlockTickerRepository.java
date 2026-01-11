package com.joojoo.api.blockTicker.domain.repository;

import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.presentation.dto.request.TickerDateResult;
import com.joojoo.api.blockTicker.presentation.dto.response.recent.RecentTickersResponse;

import java.time.LocalDate;
import java.util.List;

public interface BlockTickerRepository {
    List<BlockTicker> saveAll(List<BlockTicker> blockTickers);

    List<BlockTicker> findAllBlockTickers(List<Long> blockIds);

    void deleteByBlockIds(Long id);

    /** 2일치 날짜 조회해서 사용한 티커들 조회 */
    TickerDateResult findAllByTickerAndDate(Long userId, Long tickerId, LocalDate targetDate);

    /** TradeLog에서 사용한 티커들 조회 */
    List<BlockTicker> findAllTickersByTradeLogIds(List<Long> tradeLogIds);

    /** blockId와 연관된 티커들 조회 */
    List<BlockTicker> findAllTickersByBlockId(Long blockId);

    /** blockId와 연관된 티커들 IN절 조회 */
    List<BlockTicker> findAllTickersByBlockIdIn(List<Long> idsToDelete);

    /** 사용자가 최근 사용한 Ticker 10개 조회 */
    List<RecentTickersResponse.RecentTickersDto> findRecentTickers(Long userId);
}
