package com.joojoo.api.blockTag.presentation.dto.response.recent;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RecentTagsResponse {
    private List<RecentTagsDto> tags;

    public static RecentTagsResponse of(List<RecentTagsDto> tags) {
        return new RecentTagsResponse(tags);
    }

    @Getter
    @AllArgsConstructor
    public static class RecentTagsDto {
        private Long tagId;
        private String tagName;
    }

}
