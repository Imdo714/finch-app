package com.joojoo.api.block.application.detail;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.presentation.dto.response.detail.BlockDetailResponseDto;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockDtoAssemblerImpl implements BlockDtoAssembler {

    @Override
    public BlockDetailResponseDto assembleTree(Long rootId, List<Block> blocks, List<BlockTag> tags, List<BlockTicker> tickers) {
        Map<Long, List<BlockDetailResponseDto.MetadataResponse>> tagMap = createMetadataMap(tags,
                bt -> bt.getBlock().getId(),
                bt -> createMetadataDto(bt.getTag().getId(), bt.getTag().getName(), bt.getSequence(), bt.getStartOffset()));

        Map<Long, List<BlockDetailResponseDto.MetadataResponse>> tickerMap = createMetadataMap(tickers,
                bt -> bt.getBlock().getId(),
                bt -> createMetadataDto(bt.getTicker().getId(), bt.getTicker().getName(), bt.getSequence(), bt.getStartOffset()));

        Map<Long, BlockDetailResponseDto> dtoMap = createBlockDetailResponseDto(blocks, tagMap, tickerMap);

        BlockDetailResponseDto rootDto = null;
        for (Block b : blocks) {
            BlockDetailResponseDto currentDto = dtoMap.get(b.getId());
            if (b.getId().equals(rootId)) {
                rootDto = currentDto;
            } else if (b.getParent() != null) {
                BlockDetailResponseDto parentDto = dtoMap.get(b.getParent().getId());
                if (parentDto != null) {
                    parentDto.getChildren().add(currentDto);
                }
            }
        }

        return rootDto;
    }

    /** BlockDetailResponseDto 응답 값 생성해주는 메서드 */
    private Map<Long, BlockDetailResponseDto> createBlockDetailResponseDto(
            List<Block> blocks,
            Map<Long, List<BlockDetailResponseDto.MetadataResponse>> tagMap,
            Map<Long, List<BlockDetailResponseDto.MetadataResponse>> tickerMap) {

        return blocks.stream()
                .collect(Collectors.toMap(
                        Block::getId,
                        b -> BlockDetailResponseDto.builder()
                                .blockId(b.getId())
                                .content(b.getContent())
                                .createdAt(b.getCreatedAt())
                                .tagNames(tagMap.getOrDefault(b.getId(), new ArrayList<>()))
                                .tickerNames(tickerMap.getOrDefault(b.getId(), new ArrayList<>()))
                                .children(new ArrayList<>())
                                .build()
                ));
    }

    /** 공통 맵 생성 메서드 BlockTag, BlockTicker 뭐가 들어올지 모르니 제네릭 활용 */
    private <T> Map<Long, List<BlockDetailResponseDto.MetadataResponse>> createMetadataMap(
            List<T> items,
            Function<T, Long> blockIdExtractor,
            Function<T, BlockDetailResponseDto.MetadataResponse> mapper
    ) {
        return items.stream()
                .collect(Collectors.groupingBy(
                        blockIdExtractor,
                        Collectors.collectingAndThen(
                                Collectors.mapping(mapper, Collectors.toList()),
                                list -> {
                                    list.sort(Comparator.comparing(BlockDetailResponseDto.MetadataResponse::getSequence));
                                    return list;
                                }
                        )
                ));
    }

    /** MetadataResponse DTO로 만들어주는 메서드 */
    private BlockDetailResponseDto.MetadataResponse createMetadataDto(Long id, String name, Integer seq, Integer offset) {
        return BlockDetailResponseDto.MetadataResponse.builder()
                .id(id)
                .name(name)
                .sequence(seq)
                .startOffset(offset)
                .build();
    }

}
