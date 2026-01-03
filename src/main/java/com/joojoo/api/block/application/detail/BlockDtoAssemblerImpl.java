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
        Map<Long, List<BlockDetailResponseDto.MetadataResponse>> tagMap = createTagMap(tags);
        Map<Long, List<BlockDetailResponseDto.MetadataResponse>> tickerMap = createTickerMap(tickers);

        Map<Long, Long> childCountMap = calculateChildCounts(blocks);

        Map<Long, BlockDetailResponseDto> dtoMap = blocks.stream()
                .collect(Collectors.toMap(
                        Block::getId,
                        block -> BlockDetailResponseDto.fromSummary
                                (
                                        block,
                                        tagMap.getOrDefault(block.getId(), new ArrayList<>()),
                                        tickerMap.getOrDefault(block.getId(), new ArrayList<>()),
                                        childCountMap.getOrDefault(block.getId(), 0L)
                                )
                        )
                );

        return buildTreeAndGetRoot(rootId, blocks, dtoMap);
    }

    /** Tag 전용 맵 생성 */
    private Map<Long, List<BlockDetailResponseDto.MetadataResponse>> createTagMap(List<BlockTag> tags) {
        return createMetadataMap(tags, bt -> bt.getBlock().getId(),
                bt -> BlockDetailResponseDto.MetadataResponse.of(bt.getTag().getId(), bt.getTag().getName(), bt.getSequence(), bt.getStartOffset()));
    }

    /** Ticker 전용 맵 생성 */
    private Map<Long, List<BlockDetailResponseDto.MetadataResponse>> createTickerMap(List<BlockTicker> tickers) {
        return createMetadataMap(tickers, bt -> bt.getBlock().getId(),
                bt -> BlockDetailResponseDto.MetadataResponse.of(bt.getTicker().getId(), bt.getTicker().getName(), bt.getSequence(), bt.getStartOffset()));
    }

    /** 자식 개수 계산 로직 분리 */
    private Map<Long, Long> calculateChildCounts(List<Block> blocks) {
        return blocks.stream()
                .filter(b -> b.getParent() != null)
                .collect(Collectors.groupingBy(
                        b -> b.getParent().getId(),
                        Collectors.counting()
                ));
    }

    /** 공통 제네릭 맵 생성 및 정렬 로직 */
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

    /** 트리 조립 로직 분리 */
    private BlockDetailResponseDto buildTreeAndGetRoot(Long rootId, List<Block> blocks, Map<Long, BlockDetailResponseDto> dtoMap) {
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

}
