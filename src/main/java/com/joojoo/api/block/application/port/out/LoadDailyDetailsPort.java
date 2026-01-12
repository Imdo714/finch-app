package com.joojoo.api.block.application.port.out;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;

import java.time.LocalDate;
import java.util.List;

public interface LoadDailyDetailsPort { // 여러 데이터를 로드하는 역할을 명시

    /** 사용자의 데이터가 존재하는 최근 Block 날짜 리스트 조회 */
    List<LocalDate> getTargetBlockDates(Long userId, LocalDate lastDate, int dateCount);

    /** 사용자의 데이터가 존재하는 최근 TradeLog 날짜 리스트 조회 */
    List<LocalDate> getTradeLogDates(Long userId, LocalDate lastDate, int dateCount);

    /** 특정 날짜들에 해당하는 사용자의 루트 블록(Block) 목록 조회 */
    List<Block> findBlocksByDates(Long userId, List<LocalDate> dates);

    /** 특정 날짜들에 해당하는 사용자의 매매 일지(TradeLog) 목록 조회 */
    List<TradeLog> findTradeLogsByDates(Long userId, List<LocalDate> dates);

    /** 조회된 블록 및 매매 일지에 연결된 모든 태그(Tag) 일괄 조회 */
    List<BlockTag> findAllBlockTags(List<Long> blockIds, List<Long> tradeLogIds);

    /** 조회된 블록 및 매매 일지에 연결된 모든 티커(Ticker) 일괄 조회 */
    List<BlockTicker> findAllBlockTickers(List<Long> blockIds, List<Long> tradeLogIds);
}
