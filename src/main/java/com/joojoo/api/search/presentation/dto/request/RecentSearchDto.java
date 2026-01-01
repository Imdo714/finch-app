package com.joojoo.api.search.presentation.dto.request;

import com.joojoo.api.search.domain.entity.SearchHistory;
import com.joojoo.global.common.enums.SearchTarget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class RecentSearchDto {
    private Long recentSearchId;
    private SearchTarget targetType;
    private Long targetId;
    private String name;

    public static RecentSearchDto of(SearchHistory history) {
        String name = "";
        Long targetId = null;

        if (history.getTargetType() == SearchTarget.TICKER && history.getTicker() != null) {
            name = history.getTicker().getName();
            targetId = history.getTicker().getId();
        } else if (history.getTargetType() == SearchTarget.TAG && history.getTag() != null) {
            name = history.getTag().getName();
            targetId = history.getTag().getId();
        }

        return RecentSearchDto.builder()
                .recentSearchId(history.getId())
                .targetType(history.getTargetType())
                .targetId(targetId)
                .name(name)
                .build();
    }
}
