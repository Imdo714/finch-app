package com.joojoo.api.blockTicker.domain.repository;

import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.presentation.dto.request.TickerDateResult;

import java.time.LocalDate;
import java.util.List;

public interface BlockTickerRepository {
    List<BlockTicker> saveAll(List<BlockTicker> blockTickers);

    List<BlockTicker> findAllBlockTickers(List<Long> blockIds);

    void deleteByBlockIds(Long id);

    /** 2일치 날짜 조회해서 사용한 티커들 조회 */
    TickerDateResult findAllByTickerAndDate(Long userId, Long tickerId, LocalDate targetDate);
}
