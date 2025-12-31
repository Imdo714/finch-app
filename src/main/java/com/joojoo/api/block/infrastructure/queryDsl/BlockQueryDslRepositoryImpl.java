package com.joojoo.api.block.infrastructure.queryDsl;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.model.entity.QBlock;
import com.joojoo.api.blockTag.domain.model.entity.QBlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.QBlockTicker;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BlockQueryDslRepositoryImpl implements BlockQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QBlock block = QBlock.block;
    private final QBlockTag blockTag = QBlockTag.blockTag;
    private final QBlockTicker blockTicker = QBlockTicker.blockTicker;

    @Override
    public List<Block> findAllChildrenByRootId(Long rootId) {
        List<Long> childIds = fetchChildIds(rootId);

        return queryFactory
                .selectFrom(block)
                .where(allBlocksInTree(rootId, childIds))
                .fetch();
    }

    @Override
    public Map<Long, Long> getChildCounts(List<Long> rootIds) {
        if (rootIds.isEmpty()) return Collections.emptyMap();

        List<Tuple> results = queryFactory
                .select(
                        block.parent.id,
                        block.count()
                )
                .from(block)
                .where(
                        block.parent.id.in(rootIds),
                        block.isDeleted.isFalse()
                )
                .groupBy(block.parent.id)
                .fetch();

        return results.stream().collect(Collectors.toMap(
                t -> t.get(block.parent.id),
                t -> t.get(block.count()),
                (v1, v2) -> v1
        ));
    }

    @Override
    public void withdrawByUserId(Long userId) {
        queryFactory.delete(blockTag)
                .where(blockTag.userId.eq(userId))
                .execute();

        queryFactory.delete(blockTicker)
                .where(blockTicker.userId.eq(userId))
                .execute();

        queryFactory
                .delete(block)
                .where(block.user.id.eq(userId))
                .execute();
    }

    /** 부모ID 가 있는 자식 블록 ID만 조회합니다. */
    private List<Long> fetchChildIds(Long rootId) {
        return queryFactory
                .select(block.id)
                .from(block)
                .where(block.parent.id.eq(rootId))
                .fetch();
    }

    /** 부모 - 자식 - 자손 을 조건으로 만들어주는 메서드 */
    private BooleanBuilder allBlocksInTree(Long rootId, List<Long> childIds) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.or(block.id.eq(rootId)); // 부모 
        builder.or(block.parent.id.eq(rootId)); // 자식 

        if (!childIds.isEmpty()) {
            builder.or(block.parent.id.in(childIds)); // 자손
        }

        return builder.and(block.isDeleted.isFalse());
    }

}
