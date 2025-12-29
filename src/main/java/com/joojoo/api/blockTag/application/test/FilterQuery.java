package com.joojoo.api.blockTag.application.test;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.model.entity.QBlock;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.model.entity.QBlockTag;
import com.joojoo.api.blockTag.presentation.dto.request.TagListDto;
import com.joojoo.api.blockTag.presentation.dto.response.all.TagDateResult;
import com.joojoo.api.blockTicker.domain.model.entity.QBlockTicker;
import com.joojoo.api.tradeLog.domain.model.entity.QTradeLog;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.global.common.enums.FilterCategory;
import com.joojoo.global.common.enums.TradeType;
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


//    public List<Block> searchBlocksWithAllKeywords(List<Long> tagIds, List<Long> tickerIds, Long userId, List<LocalDate> targetDates) {
//        if ((tickerIds == null || tickerIds.isEmpty()) && (tagIds == null || tagIds.isEmpty())) {
//            return Collections.emptyList();
//        }
//
//        JPQLQuery<Block> query = queryFactory.selectFrom(block);
//
//        // 1. 모든 티커가 포함되어야 함 (AND)
//        if (tickerIds != null && !tickerIds.isEmpty()) {
//            query.where(block.id.in(
//                    JPAExpressions.select(blockTicker.block.id)
//                            .from(blockTicker)
//                            .where(
//                                    blockTicker.userId.eq(userId),
//                                    blockTicker.ticker.id.in(tickerIds)
//                            )
//                            .groupBy(blockTicker.block.id)
//                            .having(blockTicker.ticker.id.countDistinct().eq((long) tickerIds.size()))
//            ));
//        }
//
//        // 2. 모든 태그가 포함되어야 함 (AND)
//        if (tagIds != null && !tagIds.isEmpty()) {
//            query.where(block.id.in(
//                    JPAExpressions.select(blockTag.block.id)
//                            .from(blockTag)
//                            .where(
//                                    blockTag.userId.eq(userId),
//                                    blockTag.tag.id.in(tagIds)
//                            )
//                            .groupBy(blockTag.block.id)
//                            .having(blockTag.tag.id.countDistinct().eq((long) tagIds.size()))
//            ));
//        }
//
//        // 최종 결과 반환 (최신순)
//        return query.orderBy(block.createdAt.desc()).fetch();
//    }
//
//    // TradeLog용 쿼리 (Block 쿼리와 거의 동일)
//    public List<TradeLog> searchTradeLogsWithAllKeywords(List<Long> tagIds, List<Long> tickerIds, Long userId, List<LocalDate> targetDates) {
//        if (targetDates.isEmpty()) return Collections.emptyList();
//
//        JPQLQuery<TradeLog> query = queryFactory.selectFrom(tradeLog);
//
//        if (tickerIds != null && !tickerIds.isEmpty()) {
//            query.where(tradeLog.id.in(
//                    JPAExpressions.select(blockTicker.tradeLog.id) // 여기만 tradeLog로 변경
//                            .from(blockTicker)
//                            .where(
//                                    blockTicker.userId.eq(userId),
//                                    blockTicker.ticker.id.in(tickerIds)
//                            )
//                            .groupBy(blockTicker.tradeLog.id)
//                            .having(blockTicker.ticker.id.countDistinct().eq((long) tickerIds.size()))
//            ));
//        }
//
//        if (tagIds != null && !tagIds.isEmpty()) {
//            query.where(tradeLog.id.in(
//                    JPAExpressions.select(blockTag.tradeLog.id) // 여기만 tradeLog로 변경
//                            .from(blockTag)
//                            .where(
//                                    blockTag.userId.eq(userId),
//                                    blockTag.tag.id.in(tagIds)
//                            )
//                            .groupBy(blockTag.tradeLog.id)
//                            .having(blockTag.tag.id.countDistinct().eq((long) tagIds.size()))
//            ));
//        }
//        return query.orderBy(tradeLog.createdAt.desc()).fetch();
//    }



    /** Block 전용 쿼리 */
    public List<Block> searchBlocksWithAllKeywords(List<Long> tagIds, List<Long> tickerIds, Long userId, List<LocalDate> targetDates) {
        if ((tickerIds == null || tickerIds.isEmpty()) && (tagIds == null || tagIds.isEmpty())) {
            return Collections.emptyList();
        }

        JPQLQuery<Block> query = queryFactory.selectFrom(block);

        LocalDate start = targetDates.get(targetDates.size() - 1);
        LocalDate end = targetDates.get(0);

        query.where(block.createdAt.goe(start.atStartOfDay())
                .and(block.createdAt.lt(end.plusDays(1).atStartOfDay())));

        // 1. 모든 티커가 포함되어야 함 (AND)
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

        // 2. 모든 태그가 포함되어야 함 (AND)
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
    public List<TradeLog> searchTradeLogsWithAllKeywords(List<Long> tagIds, List<Long> tickerIds, Long userId, List<LocalDate> targetDates, FilterCategory category) {
        if (targetDates.isEmpty()) return Collections.emptyList();

        JPQLQuery<TradeLog> query = queryFactory.selectFrom(tradeLog);

        LocalDate start = targetDates.get(targetDates.size() - 1);
        LocalDate end = targetDates.get(0);

        query.where(
                tradeLog.user.id.eq(userId),
                tradeLog.createdAt.goe(start.atStartOfDay()),
                tradeLog.createdAt.lt(end.plusDays(1).atStartOfDay()),
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

    /**
     * 데이터가 존재하는 실제 날짜 리스트 추출
     */
    public List<LocalDate> getTargetDates(Long userId, List<Long> tagIds, List<Long> tickerIds, LocalDate lastDate, FilterCategory category, int limit) {
        List<LocalDate> blockDates = new ArrayList<>();
        List<LocalDate> logDates = new ArrayList<>();

        // category가 "BLOCK"이거나 "ALL"인 경우만 실행
        if (category == null || category == FilterCategory.ALL || category == FilterCategory.BLOCK) {
            blockDates = queryFactory
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

        // category가 "BUY", "SELL" 또는 "ALL"인 경우 실행
        if (category == null || category == FilterCategory.ALL || category == FilterCategory.BUY || category == FilterCategory.SELL) {
            logDates = queryFactory
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

        return Stream.concat(blockDates.stream(), logDates.stream())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .limit(limit)
                .toList();
    }

    private BooleanExpression filterByTradeType(FilterCategory category) {
        if (category == FilterCategory.BUY) {
            return tradeLog.tradeType.eq(TradeType.BUY);
        } else if (category == FilterCategory.SELL) {
            return tradeLog.tradeType.eq(TradeType.SELL);
        }
        return null;
    }

    private BooleanExpression filterBlockByTags(List<Long> tagIds, Long userId) {
        if (tagIds == null || tagIds.isEmpty()) return null;
        return block.id.in(
                JPAExpressions.select(blockTag.block.id).from(blockTag)
                        .where(blockTag.tag.id.in(tagIds), blockTag.userId.eq(userId))
                        .groupBy(blockTag.block.id)
                        .having(blockTag.tag.id.countDistinct().eq((long) tagIds.size()))
        );
    }

    private BooleanExpression filterBlockByTickers(List<Long> tickerIds, Long userId) {
        if (tickerIds == null || tickerIds.isEmpty()) return null;
        return block.id.in(
                JPAExpressions.select(blockTicker.block.id).from(blockTicker)
                        .where(blockTicker.ticker.id.in(tickerIds), blockTicker.userId.eq(userId))
                        .groupBy(blockTicker.block.id)
                        .having(blockTicker.ticker.id.countDistinct().eq((long) tickerIds.size()))
        );
    }

    private BooleanExpression filterLogByTags(List<Long> tagIds, Long userId) {
        if (tagIds == null || tagIds.isEmpty()) return null;
        return tradeLog.id.in(
                JPAExpressions.select(blockTag.tradeLog.id).from(blockTag)
                        .where(blockTag.tag.id.in(tagIds), blockTag.userId.eq(userId))
                        .groupBy(blockTag.tradeLog.id)
                        .having(blockTag.tag.id.countDistinct().eq((long) tagIds.size()))
        );
    }

    private BooleanExpression filterLogByTickers(List<Long> tickerIds, Long userId) {
        if (tickerIds == null || tickerIds.isEmpty()) return null;
        return tradeLog.id.in(
                JPAExpressions.select(blockTicker.tradeLog.id).from(blockTicker)
                        .where(blockTicker.ticker.id.in(tickerIds), blockTicker.userId.eq(userId))
                        .groupBy(blockTicker.tradeLog.id)
                        .having(blockTicker.ticker.id.countDistinct().eq((long) tickerIds.size()))
        );
    }

}
