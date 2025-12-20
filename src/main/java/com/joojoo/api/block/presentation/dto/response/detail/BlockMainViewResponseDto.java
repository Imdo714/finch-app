package com.joojoo.api.block.presentation.dto.response.detail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class BlockMainViewResponseDto {
    private List<BlockDetailResponseDto> blockList;

    public static BlockMainViewResponseDto of(List<BlockDetailResponseDto> blockList) {
        return BlockMainViewResponseDto.builder()
                .blockList(blockList)
                .build();
    }

}
