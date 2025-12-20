package com.joojoo.api.tag.infrastructure.queryDsl;

import com.joojoo.api.tag.domain.model.entity.QTag;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class TagQueryDslRepositoryImpl implements TagQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QTag tag = QTag.tag;

    @Override
    public List<Tag> findAllByNames(Set<String> names) {
        if (names == null || names.isEmpty()) {
            return Collections.emptyList();
        }

        return queryFactory
                .selectFrom(tag)
                .where(tag.name.in(names))
                .fetch();
    }
}
