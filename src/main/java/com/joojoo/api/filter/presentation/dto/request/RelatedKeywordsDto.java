package com.joojoo.api.filter.presentation.dto.request;

import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public class RelatedKeywordsDto {
    private List<Tag> relatedTags;
    private List<Ticker> relatedTickers;

    public static RelatedKeywordsDto empty() {
        return new RelatedKeywordsDto(Collections.emptyList(), Collections.emptyList());
    }

}
