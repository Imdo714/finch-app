package com.joojoo.api.blockTag.infrastructure.queryDsl;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.model.entity.QBlockTag;
import com.joojoo.api.common.domain.response.detail.count.RelatedBlockDetailCountResponse;
import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;
import com.joojoo.api.tag.domain.model.entity.QTag;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BlockTagQueryDslRepositoryImpl implements BlockTagQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QBlockTag blockTag = QBlockTag.blockTag;
    private final QTag tag = QTag.tag;

    @Override
    public List<BlockTag> findAllBlockTags(List<Long> blockIds) {
        return queryFactory
                .selectFrom(blockTag)
                .join(blockTag.tag, tag).fetchJoin()
                .where(blockTag.block.id.in(blockIds))
                .fetch();
    }

    @Override
    public List<RecentTagsResponse.RecentTagsDto> findRecentTags(Long userId) {
        return queryFactory
                .select(Projections.constructor(RecentTagsResponse.RecentTagsDto.class,
                        tag.id,
                        tag.name
                ))
                .from(blockTag)
                .join(blockTag.tag, tag)
                .where(blockTag.userId.eq(userId))
                .groupBy(tag.id)
                .orderBy(blockTag.id.max().desc())
                .limit(10)
                .fetch();
    }

    @Override
    public RelatedBlockDetailCountResponse getBlockCount(Long userId, Long tagId) {
        return queryFactory
                .select(Projections.constructor(RelatedBlockDetailCountResponse.class,
                        tag.name,
                        blockTag.block.id.countDistinct().coalesce(0L),
                        blockTag.tradeLog.id.countDistinct().coalesce(0L)
                ))
                .from(tag)
                .leftJoin(blockTag).on(
                        blockTag.tag.id.eq(tag.id)
                                .and(blockTag.userId.eq(userId))
                )
                .where(
                        tag.id.eq(tagId)
                )
                .groupBy(tag.id, tag.name)
                .fetchOne();
    }

    @Override
    public List<BlockTag> findAllTagsByTradeLogIds(List<Long> tradeLogIds) {
        if (tradeLogIds.isEmpty()) return Collections.emptyList();
        return queryFactory
                .selectFrom(blockTag)
                .join(blockTag.tag).fetchJoin()
                .where(blockTag.tradeLog.id.in(tradeLogIds))
                .fetch();
    }

    @Override
    public List<BlockTag> findAllByBlockIdIn(List<Long> blockIds) {
        if (blockIds == null || blockIds.isEmpty()) {
            return Collections.emptyList();
        }

        return queryFactory
                .selectFrom(blockTag)
                .join(blockTag.tag, tag).fetchJoin()
                .where(blockTag.block.id.in(blockIds))
                .fetch();
    }

    @Override
    public List<BlockTag> findAllTagsByBlockId(Long blockId) {
        if (blockId == null) {
            return Collections.emptyList();
        }

        return queryFactory
                .selectFrom(blockTag)
                .join(blockTag.tag, tag).fetchJoin()
                .where(blockTag.block.id.eq(blockId))
                .fetch();
    }

    @Override
    public void deleteByTradeLogId(Long tradeLogId) {
        queryFactory.delete(blockTag)
                .where(blockTag.tradeLog.id.eq(tradeLogId))
                .execute();
    }
}
