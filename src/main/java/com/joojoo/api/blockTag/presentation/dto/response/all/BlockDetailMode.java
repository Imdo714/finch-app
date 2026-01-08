package com.joojoo.api.blockTag.presentation.dto.response.all;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.presentation.dto.response.detail.BlockDetailResponseDto;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.common.domain.enums.TagSourceType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BlockDetailMode {
    private Long blockId;
    private String content;
    private LocalDateTime createdAt;
    private List<MetadataResponse> tagNames;
    private List<MetadataResponse> tickerNames;
    private Long childCount;
    private List<BlockDetailResponseDto> children;

    public static BlockDetailMode from(Block block, List<BlockTag> tagsForThisBlock, List<BlockTicker> tickersForThisBlock, Long childCount) {
        return BlockDetailMode.builder()
                .blockId(block.getId())
                .content(block.getContent())
                .createdAt(block.getCreatedAt())
                .tagNames(tagsForThisBlock.stream()
                        .map(bt -> MetadataResponse.of(
                                bt.getTag().getId(),
                                bt.getTag().getName(),
                                bt.getFieldType(),
                                bt.getSequence(),
                                bt.getStartOffset()))
                        .toList())
                .tickerNames(tickersForThisBlock.stream()
                        .map(bt -> MetadataResponse.of(
                                bt.getTicker().getId(),
                                bt.getTicker().getName(),
                                bt.getFieldType(),
                                bt.getSequence(),
                                bt.getStartOffset()))
                        .toList())
                .childCount(childCount != null ? childCount : 0L) // Null 방어
                .children(new ArrayList<>())
                .build();
    }

    @Getter
    @Builder
    public static class MetadataResponse {
        private Long id;
        private String name;
        private TagSourceType fieldType;
        private Integer sequence;
        private Integer startOffset;

        public static MetadataResponse of(Long id, String name, TagSourceType fieldType, Integer sequence, Integer startOffset) {
            return MetadataResponse.builder()
                    .id(id)
                    .name(name)
                    .fieldType(fieldType)
                    .sequence(sequence)
                    .startOffset(startOffset)
                    .build();
        }
    }
}
