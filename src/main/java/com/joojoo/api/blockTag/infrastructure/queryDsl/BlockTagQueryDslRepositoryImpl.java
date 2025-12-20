package com.joojoo.api.blockTag.infrastructure.queryDsl;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.model.entity.QBlockTag;
import com.joojoo.api.tag.domain.model.entity.QTag;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
