package com.joojoo.api.block.presentation.dto.response.detail;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BlockDetailResponseDto {
    private Long blockId;
    private String content;
    private LocalDateTime createdAt;
    private List<MetadataResponse> tagNames;
    private List<MetadataResponse> tickerNames;
    private List<BlockDetailResponseDto> children;

    @Getter
    @Builder
    public static class MetadataResponse {
        private Long id;
        private String name;
        private Integer sequence;
        private Integer startOffset;

        public static MetadataResponse of(Long id, String name, Integer sequence, Integer startOffset) {
            return MetadataResponse.builder()
                    .id(id)
                    .name(name)
                    .sequence(sequence)
                    .startOffset(startOffset)
                    .build();
        }
    }
}
