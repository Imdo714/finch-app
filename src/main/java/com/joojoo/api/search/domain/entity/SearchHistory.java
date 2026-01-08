package com.joojoo.api.search.domain.entity;

import com.joojoo.api.common.domain.entity.BaseCreateEntity;
import com.joojoo.api.common.domain.enums.SearchTarget;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.user.domain.model.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "search_histories")
public class SearchHistory extends BaseCreateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private SearchTarget targetType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticker_id")
    private Ticker ticker;

    @Builder
    public SearchHistory(User user, SearchTarget targetType, Tag tag, Ticker ticker) {
        this.user = user;
        this.targetType = targetType;
        this.tag = tag;
        this.ticker = ticker;
    }

    public static SearchHistory of(User user, SearchTarget type, Tag tag, Ticker ticker){
        return SearchHistory.builder()
                .user(user)
                .targetType(type)
                .tag(tag)
                .ticker(ticker)
                .build();
    }

    public static SearchHistory createTickerHistory(User user, Ticker ticker) {
        return new SearchHistory(user, SearchTarget.TICKER, null, ticker);
    }

    public static SearchHistory createTagHistory(User user, Tag tag) {
        return new SearchHistory(user, SearchTarget.TAG, tag, null);
    }

}
