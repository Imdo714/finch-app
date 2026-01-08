package com.joojoo.api.search.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class TagHistoryResponseDto {
    private List<TagElement> history;

    public static TagHistoryResponseDto of(List<TagElement> history) {
        return new TagHistoryResponseDto(history);
    }

    public static TagHistoryResponseDto from(Set<String> rawStrings) {
        if (rawStrings == null || rawStrings.isEmpty()) {
            return TagHistoryResponseDto.of(Collections.emptyList());
        }

        return TagHistoryResponseDto.of( new ArrayList<>(rawStrings.stream()
                .map(TagElement::fromRawString)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        TagElement::getTagId,
                        element -> element,
                        (existing, replacement) -> existing
                ))
                .values())
        );
    }

    @Getter
    @AllArgsConstructor
    public static class TagElement {
        private Long tagId;
        private String tagName;

        public static TagElement fromRawString(String raw) {
            try {
                /** ":"로 나누어 userId 부분 제외 */
                String dataPart = raw.split(":")[1];

                /** "*"로 나누어 내용 추출 ["ㅇㄴ", "오늘", "3"] */
                String[] details = dataPart.split("\\*");

                return new TagElement(
                        Long.parseLong(details[2]),
                        details[1]
                );
            } catch (Exception e) {
                return null;
            }
        }
    }

}
