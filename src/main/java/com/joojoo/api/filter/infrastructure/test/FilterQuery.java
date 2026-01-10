package com.joojoo.api.filter.infrastructure.test;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.model.entity.QBlock;
import com.joojoo.api.blockTag.domain.model.entity.QBlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.QBlockTicker;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.tradeLog.domain.model.entity.QTradeLog;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.api.filter.presentation.dto.request.RelatedKeywordsDto;
import com.joojoo.api.common.domain.enums.FilterCategory;
import com.joojoo.api.common.domain.enums.TradeType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class FilterQuery {

    private final JPAQueryFactory queryFactory;
    private final QBlockTag blockTag = QBlockTag.blockTag;
    private final QBlockTicker blockTicker = QBlockTicker.blockTicker;
    private final QBlock block = QBlock.block;
    private final QTradeLog tradeLog = QTradeLog.tradeLog;

    /** Block 전용 쿼리 */
    public List<Block> searchBlocksWithAllKeywordsCount(List<Long> tagIds, List<Long> tickerIds, Long userId) {
        if ((tickerIds == null || tickerIds.isEmpty()) && (tagIds == null || tagIds.isEmpty())) {
            return Collections.emptyList();
        }

        JPQLQuery<Block> query = queryFactory.selectFrom(block);

        if (tickerIds != null && !tickerIds.isEmpty()) {
            query.where(block.id.in(
                    JPAExpressions.select(blockTicker.block.id)
                            .from(blockTicker)
                            .where(
                                    blockTicker.userId.eq(userId),
                                    blockTicker.ticker.id.in(tickerIds)
                            )
                            .groupBy(blockTicker.block.id)
                            .having(blockTicker.ticker.id.countDistinct().eq((long) tickerIds.size()))
            ));
        }

        if (tagIds != null && !tagIds.isEmpty()) {
            query.where(block.id.in(
                    JPAExpressions.select(blockTag.block.id)
                            .from(blockTag)
                            .where(
                                    blockTag.userId.eq(userId),
                                    blockTag.tag.id.in(tagIds)
                            )
                            .groupBy(blockTag.block.id)
                            .having(blockTag.tag.id.countDistinct().eq((long) tagIds.size()))
            ));
        }

        return query.orderBy(block.createdAt.desc()).fetch();
    }

    /** TradeLog 전용 쿼리 */
    public List<TradeLog> searchTradeLogsWithAllKeywordsCount(List<Long> tagIds, List<Long> tickerIds, Long userId, FilterCategory category) {
        if ((tickerIds == null || tickerIds.isEmpty()) && (tagIds == null || tagIds.isEmpty())) {
            return Collections.emptyList();
        }

        JPQLQuery<TradeLog> query = queryFactory.selectFrom(tradeLog)
                .where(
                        filterByTradeType(category)
                );

        if (tickerIds != null && !tickerIds.isEmpty()) {
            query.where(tradeLog.id.in(
                    JPAExpressions.select(blockTicker.tradeLog.id)
                            .from(blockTicker)
                            .where(
                                    blockTicker.userId.eq(userId),
                                    blockTicker.ticker.id.in(tickerIds)
                            )
                            .groupBy(blockTicker.tradeLog.id)
                            .having(blockTicker.ticker.id.countDistinct().eq((long) tickerIds.size()))
            ));
        }

        if (tagIds != null && !tagIds.isEmpty()) {
            query.where(tradeLog.id.in(
                    JPAExpressions.select(blockTag.tradeLog.id)
                            .from(blockTag)
                            .where(
                                    blockTag.userId.eq(userId),
                                    blockTag.tag.id.in(tagIds)
                            )
                            .groupBy(blockTag.tradeLog.id)
                            .having(blockTag.tag.id.countDistinct().eq((long) tagIds.size()))
            ));
        }
        return query.orderBy(tradeLog.createdAt.desc()).fetch();
    }


    /** 태그 기준으로  연관된 태그, 티커들 반환 리스트 */
    public RelatedKeywordsDto findRelatedKeywordsByTag(Long userId, Long targetTagId) {
        // 1. 기준 태그(3번)가 포함된 모든 게시물의 ID를 추출 (중복 제거)
        List<Long> blockIds = queryFactory
                .select(blockTag.block.id)
                .from(blockTag)
                .where(
                        blockTag.userId.eq(userId),
                        blockTag.tag.id.eq(targetTagId),
                        blockTag.block.id.isNotNull()
                )
                .distinct().fetch();

        List<Long> logIds = queryFactory
                .select(blockTag.tradeLog.id)
                .from(blockTag)
                .where(
                        blockTag.userId.eq(userId),
                        blockTag.tag.id.eq(targetTagId),
                        blockTag.tradeLog.id.isNotNull()
                )
                .distinct().fetch();

        if (blockIds.isEmpty() && logIds.isEmpty()) {
            return new RelatedKeywordsDto(Collections.emptyList(), Collections.emptyList());
        }

        // 2. 해당 게시물들에 달린 '모든 다른 태그' 조회 (자기 자신 제외)
        List<Tag> relatedTags = queryFactory
                .select(blockTag.tag)
                .from(blockTag)
                .where(
                        blockTag.userId.eq(userId),
                        blockTag.block.id.in(blockIds)
                                .or(blockTag.tradeLog.id.in(logIds))
//                        blockTag.tag.id.ne(targetTagId) // 3번 태그 본인은 결과에서 제외
                )
                .distinct()
                .fetch();

        // 3. 해당 게시물들에 달린 '모든 티커' 조회
        List<Ticker> relatedTickers = queryFactory
                .select(blockTicker.ticker)
                .from(blockTicker)
                .where(
                        blockTicker.userId.eq(userId),
                        blockTicker.block.id.in(blockIds).or(blockTicker.tradeLog.id.in(logIds))
                )
                .distinct()
                .fetch();

        return new RelatedKeywordsDto(relatedTags, relatedTickers);
    }

    public RelatedKeywordsDto findRelatedKeywordsByTicker(Long userId, Long targetTickerId) {
        // 1. 기준 티커가 포함된 모든 게시물 ID 추출 (BlockTicker 테이블 기준)
        List<Long> blockIds = queryFactory
                .select(blockTicker.block.id)
                .from(blockTicker)
                .where(
                        blockTicker.userId.eq(userId),
                        blockTicker.ticker.id.eq(targetTickerId),
                        blockTicker.block.id.isNotNull()
                ).distinct().fetch();

        List<Long> logIds = queryFactory
                .select(blockTicker.tradeLog.id)
                .from(blockTicker)
                .where(
                        blockTicker.userId.eq(userId),
                        blockTicker.ticker.id.eq(targetTickerId),
                        blockTicker.tradeLog.id.isNotNull()
                ).distinct().fetch();

        if (blockIds.isEmpty() && logIds.isEmpty()) {
            return new RelatedKeywordsDto(Collections.emptyList(), Collections.emptyList());
        }

        // 2. 해당 게시물들에 달린 모든 연관 태그 조회
        List<Tag> tags = queryFactory
                .select(blockTag.tag)
                .from(blockTag)
                .where(
                        blockTag.userId.eq(userId),
                        blockTag.block.id.in(blockIds).or(blockTag.tradeLog.id.in(logIds))
                ).distinct().fetch();

        List<Ticker> tickers = queryFactory
                .select(blockTicker.ticker)
                .from(blockTicker)
                .where(
                        blockTicker.userId.eq(userId),
                        blockTicker.block.id.in(blockIds).or(blockTicker.tradeLog.id.in(logIds))
//                        blockTicker.ticker.id.ne(targetTickerId) // 자기 자신 제외
                ).distinct().fetch();

        return new RelatedKeywordsDto(tags, tickers);
    }

    private BooleanExpression filterByTradeType(FilterCategory category) {
        if (category == FilterCategory.BUY) {
            return tradeLog.tradeType.eq(TradeType.BUY);
        } else if (category == FilterCategory.SELL) {
            return tradeLog.tradeType.eq(TradeType.SELL);
        }
        return null;
    }

}
