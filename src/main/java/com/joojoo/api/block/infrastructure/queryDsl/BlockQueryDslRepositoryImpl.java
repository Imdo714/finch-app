package com.joojoo.api.block.infrastructure.queryDsl;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.model.entity.QBlock;
import com.joojoo.global.exception.handleException.block.BlockNotFoundException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BlockQueryDslRepositoryImpl implements BlockQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QBlock block = QBlock.block;

    @Override
    public List<Block> findAllChildrenByRootId(Long rootId) {
        List<Long> childIds = fetchChildIds(rootId);

        return queryFactory
                .selectFrom(block)
                .where(allBlocksInTree(rootId, childIds))
                .fetch();
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
