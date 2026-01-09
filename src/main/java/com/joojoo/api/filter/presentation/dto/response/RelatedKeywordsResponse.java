package com.joojoo.api.filter.presentation.dto.response;

import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatedKeywordsResponse {
    private List<TagInfo> relatedTags;
    private List<TickerInfo> relatedTickers;

    @Getter
    @AllArgsConstructor
    public static class TagInfo {
        private Long id;
        private String name;
    }

    @Getter
    @AllArgsConstructor
    public static class TickerInfo {
        private Long id;
        private String name;
    }

    public static RelatedKeywordsResponse of(List<Tag> tags, List<Ticker> tickers) {
        return RelatedKeywordsResponse.builder()
                .relatedTags(tags.stream()
                        .map(t -> new TagInfo(t.getId(), t.getName()))
                        .toList())
                .relatedTickers(tickers.stream()
                        .map(t -> new TickerInfo(t.getId(), t.getName()))
                        .toList())
                .build();
    }
}
