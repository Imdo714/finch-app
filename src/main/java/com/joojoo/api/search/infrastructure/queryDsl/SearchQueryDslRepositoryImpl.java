package com.joojoo.api.search.infrastructure.queryDsl;

import com.joojoo.api.search.domain.entity.QSearchHistory;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.global.common.enums.SearchTarget;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SearchQueryDslRepositoryImpl implements SearchQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QSearchHistory searchHistory = QSearchHistory.searchHistory;

    @Override
    public void deleteIfExists(Long userId, SearchTarget type, Tag tag, Ticker ticker) {
        queryFactory
                .delete(searchHistory)
                .where(
                        searchHistory.user.id.eq(userId),
                        searchHistory.targetType.eq(type),
                        // 동적 조건 적용
                        tag != null ? searchHistory.tag.eq(tag) : searchHistory.ticker.eq(ticker)
                )
                .execute();
    }

}
