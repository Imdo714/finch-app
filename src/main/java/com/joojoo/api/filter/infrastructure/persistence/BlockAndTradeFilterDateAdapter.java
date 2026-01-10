package com.joojoo.api.filter.infrastructure.persistence;

import com.joojoo.api.block.domain.model.entity.QBlock;
import com.joojoo.api.blockTag.domain.model.entity.QBlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.QBlockTicker;
import com.joojoo.api.common.domain.enums.FilterCategory;
import com.joojoo.api.common.domain.enums.TradeType;
import com.joojoo.api.filter.application.port.out.BlockAndTradeFilterDatePort;
import com.joojoo.api.tradeLog.domain.model.entity.QTradeLog;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class BlockAndTradeFilterDateAdapter implements BlockAndTradeFilterDatePort {

    private final JPAQueryFactory queryFactory;
    private final QBlockTag blockTag = QBlockTag.blockTag;
    private final QBlockTicker blockTicker = QBlockTicker.blockTicker;
    private final QBlock block = QBlock.block;
    private final QTradeLog tradeLog = QTradeLog.tradeLog;

    @Override
    public List<LocalDate> findTargetDates(Long userId, List<Long> tagIds, List<Long> tickerIds,
                                           LocalDate lastDate, FilterCategory category, int limit
    ) {
        // 카테고리가 "BLOCK"을 포함하는 경우 블록 데이터의 날짜 조회
        List<LocalDate> blockDates = category.isBlockApplicable() ?
                getBlockDates(userId, tagIds, tickerIds, lastDate) : Collections.emptyList();

        // 카테고리가 "BUY/SELL"을 포함하는 경우 매매 일지의 날짜 조회
        List<LocalDate> tradeLogDates = category.isTradeLogApplicable() ?
                getTradeLogDates(userId, tagIds, tickerIds, lastDate, category) : Collections.emptyList();

        // 두 날짜를 통합, 중복 제거 후 최신순으로 정렬하여 limit만큼 반환
        return Stream.concat(blockDates.stream(), tradeLogDates.stream())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .limit(limit)
                .toList();
    }

    /** 특정 조건에 맞는 매매 일지(TradeLog)들의 생성일(LocalDate) 목록을 조회합니다. */
    private List<LocalDate> getTradeLogDates(Long userId, List<Long> tagIds, List<Long> tickerIds, LocalDate lastDate, FilterCategory category) {
        return queryFactory
                .select(tradeLog.createdAt)
                .from(tradeLog)
                .where(
                        tradeLog.user.id.eq(userId),
                        lastDate == null ? null : tradeLog.createdAt.lt(lastDate.plusDays(1).atStartOfDay()),
                        // category가 BUY나 SELL이면 해당 타입만 필터링
                        filterByTradeType(category),
                        filterLogByTags(tagIds, userId),
                        filterLogByTickers(tickerIds, userId)
                )
                .fetch().stream().map(LocalDateTime::toLocalDate).distinct().toList();
    }

    /** 특정 조건에 맞는 블록(Block)들의 생성일(LocalDate) 목록을 조회합니다. */
    private List<LocalDate> getBlockDates(Long userId, List<Long> tagIds, List<Long> tickerIds, LocalDate lastDate) {
        return queryFactory
                .select(block.createdAt)
                .from(block)
                .where(
                        block.user.id.eq(userId),
                        lastDate == null ? null : block.createdAt.lt(lastDate.plusDays(1).atStartOfDay()),
                        filterBlockByTags(tagIds, userId),
                        filterBlockByTickers(tickerIds, userId)
                )
                .fetch().stream().map(LocalDateTime::toLocalDate).distinct().toList();
    }

    /** 매매 유형(BUY/SELL)에 따른 필터 조건을 생성합니다. */
    private BooleanExpression filterByTradeType(FilterCategory category) {
        if (category == FilterCategory.BUY) return tradeLog.tradeType.eq(TradeType.BUY);
        if (category == FilterCategory.SELL) return tradeLog.tradeType.eq(TradeType.SELL);
        return null;
    }

    /** 블록이 요청된 모든 태그를 포함하고 있는지 확인하는 서브쿼리 조건을 생성합니다. */
    private BooleanExpression filterBlockByTags(List<Long> tagIds, Long userId) {
        if (isEmpty(tagIds)) return null;
        return block.id.in(
                JPAExpressions.select(blockTag.block.id).from(blockTag)
                        .where(blockTag.tag.id.in(tagIds), blockTag.userId.eq(userId))
                        .groupBy(blockTag.block.id)
                        .having(blockTag.tag.id.countDistinct().eq((long) tagIds.size()))
        );
    }

    /** 블록이 요청된 모든 티커를 포함하고 있는지 확인하는 서브쿼리 조건을 생성합니다. */
    private BooleanExpression filterBlockByTickers(List<Long> tickerIds, Long userId) {
        if (isEmpty(tickerIds)) return null;
        return block.id.in(
                JPAExpressions.select(blockTicker.block.id).from(blockTicker)
                        .where(blockTicker.ticker.id.in(tickerIds), blockTicker.userId.eq(userId))
                        .groupBy(blockTicker.block.id)
                        .having(blockTicker.ticker.id.countDistinct().eq((long) tickerIds.size()))
        );
    }

    /** 매매 일지가 요청된 모든 태그를 포함하고 있는지 확인하는 서브쿼리 조건을 생성합니다. */
    private BooleanExpression filterLogByTags(List<Long> tagIds, Long userId) {
        if (isEmpty(tagIds)) return null;
        return tradeLog.id.in(
                JPAExpressions.select(blockTag.tradeLog.id).from(blockTag)
                        .where(blockTag.tag.id.in(tagIds), blockTag.userId.eq(userId))
                        .groupBy(blockTag.tradeLog.id)
                        .having(blockTag.tag.id.countDistinct().eq((long) tagIds.size()))
        );
    }

    /** 매매 일지가 요청된 모든 티커를 포함하고 있는지 확인하는 서브쿼리 조건을 생성합니다. */
    private BooleanExpression filterLogByTickers(List<Long> tickerIds, Long userId) {
        if (isEmpty(tickerIds)) return null;
        return tradeLog.id.in(
                JPAExpressions.select(blockTicker.tradeLog.id).from(blockTicker)
                        .where(blockTicker.ticker.id.in(tickerIds), blockTicker.userId.eq(userId))
                        .groupBy(blockTicker.tradeLog.id)
                        .having(blockTicker.ticker.id.countDistinct().eq((long) tickerIds.size()))
        );
    }

    /** 특정 날짜(lastDate) 이전의 데이터만 조회하는 조건을 생성합니다. (TradeLog용) */
    private BooleanExpression lastDateBefore(LocalDate lastDate) {
        return lastDate == null ? null : tradeLog.createdAt.lt(lastDate.plusDays(1).atStartOfDay());
    }

    /** 특정 날짜(lastDate) 이전의 데이터만 조회하는 조건을 생성합니다. (Block용) */
    private BooleanExpression lastDateBeforeForBlock(LocalDate lastDate) {
        return lastDate == null ? null : block.createdAt.lt(lastDate.plusDays(1).atStartOfDay());
    }

    private boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
}
