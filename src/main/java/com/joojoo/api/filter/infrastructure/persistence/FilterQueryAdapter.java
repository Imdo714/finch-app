package com.joojoo.api.filter.infrastructure.persistence;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.model.entity.QBlock;
import com.joojoo.api.blockTag.domain.model.entity.QBlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.QBlockTicker;
import com.joojoo.api.common.domain.enums.FilterCategory;
import com.joojoo.api.common.domain.enums.TradeType;
import com.joojoo.api.filter.application.port.out.FilterQueryPort;
import com.joojoo.api.tradeLog.domain.model.entity.QTradeLog;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FilterQueryAdapter implements FilterQueryPort {

    private final JPAQueryFactory queryFactory;
    private final QBlockTag blockTag = QBlockTag.blockTag;
    private final QBlockTicker blockTicker = QBlockTicker.blockTicker;
    private final QBlock block = QBlock.block;
    private final QTradeLog tradeLog = QTradeLog.tradeLog;

    /** 지정된 티커와 태그를 모두 포함하는 블록(Block) 목록을 조회 */
    @Override
    public List<Block> findBlocksByCriteria(List<Long> tagIds, List<Long> tickerIds, Long userId, List<LocalDate> targetDates) {
        if (isFilterEmpty(tagIds, tickerIds)) {
            return Collections.emptyList();
        }

        return queryFactory.selectFrom(block)
                .where(
                        blockCreatedAtBetween(targetDates),
                        hasAllTickerIdsInBlock(tickerIds, userId),
                        hasAllTagIdsInBlock(tagIds, userId)
                )
                .orderBy(block.createdAt.desc())
                .fetch();
    }

    /** 지정된 티커와 태그를 모두 포함하는 매매 일지(TradeLog) 목록을 조회 */
    @Override
    public List<TradeLog> findTradeLogsByCriteria(List<Long> tagIds, List<Long> tickerIds, Long userId, List<LocalDate> targetDates, FilterCategory category) {
        if (targetDates.isEmpty()) return Collections.emptyList();

        return queryFactory.selectFrom(tradeLog)
                .where(
                        tradeLog.user.id.eq(userId),
                        tradeLogCreatedAtBetween(targetDates),
                        filterByTradeType(category),
                        hasAllTickerIdsInTradeLog(tickerIds, userId),
                        hasAllTagIdsInTradeLog(tagIds, userId)
                )
                .orderBy(tradeLog.createdAt.desc())
                .fetch();
    }

    /** 필터 조건에 맞는 블록 전체 개수 조회 */
    @Override
    public long countBlocksByCriteria(List<Long> tagIds, List<Long> tickerIds, Long userId) {
        if (isFilterEmpty(tagIds, tickerIds)) return 0L;

        Long count = queryFactory
                .select(block.count())
                .from(block)
                .where(
                        hasAllTickerIdsInBlock(tickerIds, userId),
                        hasAllTagIdsInBlock(tagIds, userId)
                )
                .fetchOne();

        return count != null ? count : 0L;
    }

    /** 필터 조건에 맞는 매매 일지 전체 개수 조회 */
    @Override
    public long countTradeLogsByCriteria(List<Long> tagIds, List<Long> tickerIds, Long userId, FilterCategory category) {
        if (isFilterEmpty(tagIds, tickerIds)) return 0L;

        Long count = queryFactory
                .select(tradeLog.count())
                .from(tradeLog)
                .where(
                        filterByTradeType(category),
                        hasAllTickerIdsInTradeLog(tickerIds, userId),
                        hasAllTagIdsInTradeLog(tagIds, userId)
                )
                .fetchOne();

        return count != null ? count : 0L;
    }

    /** 블록 생성일이 지정된 날짜 범위 내에 있는지 확인하는 조건을 생성합니다. */
    private BooleanExpression blockCreatedAtBetween(List<LocalDate> dates) {
        if (dates == null || dates.isEmpty()) return null;
        return block.createdAt.goe(dates.get(dates.size() - 1).atStartOfDay())
                .and(block.createdAt.lt(dates.get(0).plusDays(1).atStartOfDay()));
    }

    /** 매매 일지 생성일이 지정된 날짜 범위 내에 있는지 확인하는 조건을 생성합니다. */
    private BooleanExpression tradeLogCreatedAtBetween(List<LocalDate> dates) {
        if (dates == null || dates.isEmpty()) return null;
        return tradeLog.createdAt.goe(dates.get(dates.size() - 1).atStartOfDay())
                .and(tradeLog.createdAt.lt(dates.get(0).plusDays(1).atStartOfDay()));
    }

    /** 블록이 요청된 모든 티커 ID를 가지고 있는지 확인하는 서브쿼리 조건을 생성합니다. (Relational Division 방식) */
    private BooleanExpression hasAllTickerIdsInBlock(List<Long> tickerIds, Long userId) {
        if (isEmpty(tickerIds)) return null;
        return block.id.in(
                JPAExpressions.select(blockTicker.block.id)
                        .from(blockTicker)
                        .where(blockTicker.userId.eq(userId), blockTicker.ticker.id.in(tickerIds))
                        .groupBy(blockTicker.block.id)
                        .having(blockTicker.ticker.id.countDistinct().eq((long) tickerIds.size()))
        );
    }

    /** 블록이 요청된 모든 태그 ID를 가지고 있는지 확인하는 서브쿼리 조건을 생성합니다. */
    private BooleanExpression hasAllTagIdsInBlock(List<Long> tagIds, Long userId) {
        if (isEmpty(tagIds)) return null;
        return block.id.in(
                JPAExpressions.select(blockTag.block.id)
                        .from(blockTag)
                        .where(blockTag.userId.eq(userId), blockTag.tag.id.in(tagIds))
                        .groupBy(blockTag.block.id)
                        .having(blockTag.tag.id.countDistinct().eq((long) tagIds.size()))
        );
    }

    /** 매매 일지가 요청된 모든 티커 ID를 가지고 있는지 확인하는 서브쿼리 조건을 생성합니다. */
    private BooleanExpression hasAllTickerIdsInTradeLog(List<Long> tickerIds, Long userId) {
        if (isEmpty(tickerIds)) return null;
        return tradeLog.id.in(
                JPAExpressions.select(blockTicker.tradeLog.id)
                        .from(blockTicker)
                        .where(blockTicker.userId.eq(userId), blockTicker.ticker.id.in(tickerIds))
                        .groupBy(blockTicker.tradeLog.id)
                        .having(blockTicker.ticker.id.countDistinct().eq((long) tickerIds.size()))
        );
    }

    /** 매매 일지가 요청된 모든 태그 ID를 가지고 있는지 확인하는 서브쿼리 조건을 생성합니다. */
    private BooleanExpression hasAllTagIdsInTradeLog(List<Long> tagIds, Long userId) {
        if (isEmpty(tagIds)) return null;
        return tradeLog.id.in(
                JPAExpressions.select(blockTag.tradeLog.id)
                        .from(blockTag)
                        .where(blockTag.userId.eq(userId), blockTag.tag.id.in(tagIds))
                        .groupBy(blockTag.tradeLog.id)
                        .having(blockTag.tag.id.countDistinct().eq((long) tagIds.size()))
        );
    }

    /** 카테고리(BUY/SELL)에 따라 매매 유형별 필터링 조건을 생성합니다. */
    private BooleanExpression filterByTradeType(FilterCategory category) {
        if (category == FilterCategory.BUY) return tradeLog.tradeType.eq(TradeType.BUY);
        if (category == FilterCategory.SELL) return tradeLog.tradeType.eq(TradeType.SELL);
        return null;
    }

    /** 티커와 태그 필터가 모두 비어있는지 확인합니다. */
    private boolean isFilterEmpty(List<Long> tagIds, List<Long> tickerIds) {
        return (tickerIds == null || tickerIds.isEmpty()) && (tagIds == null || tagIds.isEmpty());
    }

    /** 리스트가 null이거나 비어있는지 확인하는 메서드 */
    private boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
}
