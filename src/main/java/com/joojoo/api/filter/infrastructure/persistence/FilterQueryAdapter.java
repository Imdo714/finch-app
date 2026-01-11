package com.joojoo.api.filter.infrastructure.persistence;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.model.entity.QBlock;
import com.joojoo.api.blockTag.domain.model.entity.QBlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.QBlockTicker;
import com.joojoo.api.common.domain.enums.FilterCategory;
import com.joojoo.api.common.domain.enums.TradeType;
import com.joojoo.api.filter.application.port.out.FilterQueryPort;
import com.joojoo.api.filter.presentation.dto.request.RelatedKeywordsDto;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
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
        if (isFilterEmpty(tagIds, tickerIds)) return Collections.emptyList();

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

    /** 특정 태그가 포함된 게시물(Block/TradeLog)들을 찾아, 해당 게시물들에 함께 사용된 다른 태그와 티커들을 조회합니다. */
    @Override
    public RelatedKeywordsDto findRelatedKeywordsByTag(Long userId, Long targetTagId) {
        List<Long> blockIds = fetchBlockIdsByTag(userId, targetTagId);
        List<Long> logIds = fetchTradeLogIdsByTag(userId, targetTagId);

        if (blockIds.isEmpty() && logIds.isEmpty()) {
            return RelatedKeywordsDto.empty();
        }

        return fetchRelatedKeywords(userId, blockIds, logIds);
    }

    /** 특정 티커가 포함된 게시물(Block/TradeLog)들을 찾아, 해당 게시물들에 함께 사용된 태그와 다른 티커들을 조회합니다. */
    @Override
    public RelatedKeywordsDto findRelatedKeywordsByTicker(Long userId, Long targetTickerId) {
        List<Long> blockIds = fetchBlockIdsByTicker(userId, targetTickerId);
        List<Long> logIds = fetchTradeLogIdsByTicker(userId, targetTickerId);

        if (blockIds.isEmpty() && logIds.isEmpty()) {
            return RelatedKeywordsDto.empty();
        }

        return fetchRelatedKeywords(userId, blockIds, logIds);
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

    /** 특정 태그가 포함된 블록 ID 리스트 조회 */
    private List<Long> fetchBlockIdsByTag(Long userId, Long tagId) {
        return queryFactory
                .select(blockTag.block.id)
                .from(blockTag)
                .where(
                        blockTag.userId.eq(userId),
                        blockTag.tag.id.eq(tagId),
                        blockTag.block.id.isNotNull()
                )
                .distinct().fetch();
    }

    /** 특정 태그가 포함된 매매 일지 ID 리스트 조회 */
    private List<Long> fetchTradeLogIdsByTag(Long userId, Long tagId) {
        return queryFactory
                .select(blockTag.tradeLog.id)
                .from(blockTag)
                .where(
                        blockTag.userId.eq(userId),
                        blockTag.tag.id.eq(tagId),
                        blockTag.tradeLog.id.isNotNull()
                )
                .distinct().fetch();
    }

    /** 특정 티커가 포함된 블록 ID 리스트 조회 */
    private List<Long> fetchBlockIdsByTicker(Long userId, Long tickerId) {
        return queryFactory
                .select(blockTicker.block.id)
                .from(blockTicker)
                .where(
                        blockTicker.userId.eq(userId),
                        blockTicker.ticker.id.eq(tickerId),
                        blockTicker.block.id.isNotNull()
                )
                .distinct().fetch();
    }

    /** 특정 티커가 포함된 매매 일지 ID 리스트 조회 */
    private List<Long> fetchTradeLogIdsByTicker(Long userId, Long tickerId) {
        return queryFactory
                .select(blockTicker.tradeLog.id)
                .from(blockTicker)
                .where(
                        blockTicker.userId.eq(userId),
                        blockTicker.ticker.id.eq(tickerId),
                        blockTicker.tradeLog.id.isNotNull()
                )
                .distinct().fetch();
    }

    /** 수집된 게시물 ID 리스트를 기반으로 실제 연관된 태그와 티커 엔티티를 조회합니다. */
    private RelatedKeywordsDto fetchRelatedKeywords(Long userId, List<Long> blockIds, List<Long> logIds) {
        List<Tag> relatedTags = queryFactory
                .select(blockTag.tag)
                .from(blockTag)
                .where(blockTag.userId.eq(userId), matchBlockOrLog(blockIds, logIds))
                .distinct().fetch();

        List<Ticker> relatedTickers = queryFactory
                .select(blockTicker.ticker)
                .from(blockTicker)
                .where(blockTicker.userId.eq(userId), matchBlockOrLogForTicker(blockIds, logIds))
                .distinct().fetch();

        return new RelatedKeywordsDto(relatedTags, relatedTickers);
    }

    /** Block 또는 TradeLog ID 리스트에 포함되는지 확인하는 태그용 조건절을 생성합니다. */
    private BooleanExpression matchBlockOrLog(List<Long> blockIds, List<Long> logIds) {
        return blockTag.block.id.in(blockIds).or(blockTag.tradeLog.id.in(logIds));
    }

    /** Block 또는 TradeLog ID 리스트에 포함되는지 확인하는 티커용 조건절을 생성합니다. */
    private BooleanExpression matchBlockOrLogForTicker(List<Long> blockIds, List<Long> logIds) {
        return blockTicker.block.id.in(blockIds).or(blockTicker.tradeLog.id.in(logIds));
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
