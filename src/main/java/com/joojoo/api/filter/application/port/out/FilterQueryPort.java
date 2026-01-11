package com.joojoo.api.filter.application.port.out;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.common.domain.enums.FilterCategory;
import com.joojoo.api.filter.presentation.dto.request.RelatedKeywordsDto;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;

import java.time.LocalDate;
import java.util.List;

public interface FilterQueryPort {
    /** 지정된 티커와 태그를 모두 포함하는 블록(Block) 목록을 조회 */
    List<Block> findBlocksByCriteria(List<Long> tagIds, List<Long> tickerIds, Long userId, List<LocalDate> targetDates);

    /** 지정된 티커와 태그를 모두 포함하는 매매 일지(TradeLog) 목록을 조회 */
    List<TradeLog> findTradeLogsByCriteria(List<Long> tagIds, List<Long> tickerIds, Long userId, List<LocalDate> targetDates, FilterCategory category);

    /** 필터 조건에 맞는 블록 전체 개수 조회 */
    long countBlocksByCriteria(List<Long> tagIds, List<Long> tickerIds, Long userId);

    /** 필터 조건에 맞는 매매 일지 전체 개수 조회 */
    long countTradeLogsByCriteria(List<Long> tagIds, List<Long> tickerIds, Long userId, FilterCategory category);

    /** 특정 태그가 포함된 게시물(Block/TradeLog)들을 찾아, 해당 게시물들에 함께 사용된 다른 태그와 티커들을 조회합니다. */
    RelatedKeywordsDto findRelatedKeywordsByTag(Long userId, Long tagId);

    /** 특정 티커가 포함된 게시물(Block/TradeLog)들을 찾아, 해당 게시물들에 함께 사용된 태그와 다른 티커들을 조회합니다. */
    RelatedKeywordsDto findRelatedKeywordsByTicker(Long userId, Long tickerId);
}
