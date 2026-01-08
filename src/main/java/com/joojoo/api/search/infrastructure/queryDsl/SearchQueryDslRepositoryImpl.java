package com.joojoo.api.search.infrastructure.queryDsl;

import com.joojoo.api.common.domain.enums.SearchTarget;
import com.joojoo.api.search.domain.entity.QSearchHistory;
import com.joojoo.api.search.domain.entity.SearchHistory;
import com.joojoo.api.tag.domain.model.entity.QTag;
import com.joojoo.api.ticker.domain.model.entity.QTicker;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SearchQueryDslRepositoryImpl implements SearchQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QSearchHistory searchHistory = QSearchHistory.searchHistory;
    private final QTag tag = QTag.tag;
    private final QTicker ticker = QTicker.ticker;

    @Override
    public List<SearchHistory> findRecentByTargetType(Long userId, SearchTarget type, int limitSize) {
        JPQLQuery<SearchHistory> query = queryFactory
                .selectFrom(searchHistory)
                .where(
                        searchHistory.user.id.eq(userId),
                        searchHistory.targetType.eq(type)
                );

        if (type == SearchTarget.TICKER) {
            query.leftJoin(searchHistory.ticker, ticker).fetchJoin();
        } else if (type == SearchTarget.TAG) {
            query.leftJoin(searchHistory.tag, tag).fetchJoin();
        }

        return query
                .orderBy(searchHistory.createdAt.desc())
                .limit(limitSize)
                .fetch();
    }

    @Override
    public void deleteDuplicateHistory(SearchHistory history) {
        queryFactory
                .delete(searchHistory)
                .where(
                        searchHistory.user.id.eq(history.getUser().getId()),
                        searchHistory.targetType.eq(history.getTargetType()),
                        targetCondition(history)
                )
                .execute();
    }

    private BooleanExpression targetCondition(SearchHistory history) {
        if (history.getTargetType() == SearchTarget.TAG) {
            return searchHistory.tag.eq(history.getTag());
        }
        return searchHistory.ticker.eq(history.getTicker());
    }

}
