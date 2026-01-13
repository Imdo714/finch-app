package com.joojoo.api.block.infrastructure.persistence;

import com.joojoo.api.block.application.port.out.LoadDailyDetailsPort;
import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.model.entity.QBlock;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.model.entity.QBlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.model.entity.QBlockTicker;
import com.joojoo.api.tag.domain.model.entity.QTag;
import com.joojoo.api.ticker.domain.model.entity.QTicker;
import com.joojoo.api.tradeLog.domain.model.entity.QTradeLog;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DailyDetailsPersistenceAdapter implements LoadDailyDetailsPort {

    private final JPAQueryFactory queryFactory;
    private final QBlock block = QBlock.block;
    private final QTradeLog tradeLog = QTradeLog.tradeLog;
    private final QBlockTicker blockTicker = QBlockTicker.blockTicker;
    private final QBlockTag blockTag = QBlockTag.blockTag;
    private final QTag tag = QTag.tag;
    private final QTicker ticker = QTicker.ticker;

    @Override
    public List<LocalDate> getTargetBlockDates(Long userId, LocalDate lastDate, int dateCount) {
        return queryFactory
                .select(block.createdAt)
                .from(block)
                .where(
                        block.user.id.eq(userId),
                        leLastDate(lastDate)
                )
                .orderBy(block.createdAt.desc())
                .fetch()
                .stream()
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .limit(dateCount)
                .toList();
    }

    @Override
    public List<LocalDate> getTradeLogDates(Long userId, LocalDate lastDate, int dateCount) {
        return queryFactory
                .select(tradeLog.createdAt)
                .from(tradeLog)
                .where(
                        tradeLog.user.id.eq(userId),
                        leTradeLogLastDate(lastDate)
                )
                .fetch()
                .stream()
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .toList();
    }

    @Override
    public List<Block> findBlocksByDates(Long userId, List<LocalDate> dates) {
        return queryFactory
                .selectFrom(block)
                .where(
                        block.user.id.eq(userId),
                        block.parent.isNull(),
                        block.isDeleted.isFalse(),
                        Expressions.dateTemplate(LocalDate.class, "DATE({0})", block.createdAt).in(dates)
                )
                .orderBy(block.createdAt.desc())
                .fetch();
    }

    @Override
    public List<TradeLog> findTradeLogsByDates(Long userId, List<LocalDate> dates) {
        return queryFactory
                .selectFrom(tradeLog)
                .where(
                        tradeLog.user.id.eq(userId),
                        Expressions.dateTemplate(LocalDate.class, "DATE({0})", tradeLog.createdAt).in(dates)
                )
                .orderBy(tradeLog.createdAt.desc())
                .fetch();
    }

    @Override
    public List<BlockTag> findAllBlockTags(List<Long> blockIds, List<Long> tradeLogIds) {
        return queryFactory
                .selectFrom(blockTag)
                .join(blockTag.tag, tag).fetchJoin()
                .where(
                        blockTag.block.id.in(blockIds)
                                .or(
                                        blockTag.tradeLog.id.in(tradeLogIds)
                                )
                )
                .fetch();
    }

    @Override
    public List<BlockTicker> findAllBlockTickers(List<Long> blockIds, List<Long> tradeLogIds) {
        return queryFactory
                .selectFrom(blockTicker)
                .join(blockTicker.ticker, ticker).fetchJoin()
                .where(
                        blockTicker.block.id.in(blockIds)
                                .or(blockTicker.tradeLog.id.in(tradeLogIds))
                )
                .fetch();
    }

    private BooleanExpression leLastDate(LocalDate lastDate) {
        if (lastDate == null) return null;
        return block.createdAt.lt(lastDate.plusDays(1).atStartOfDay());
    }

    private BooleanExpression leTradeLogLastDate(LocalDate lastDate) {
        if (lastDate == null) return null;
        return tradeLog.createdAt.lt(lastDate.plusDays(1).atStartOfDay());
    }

}
