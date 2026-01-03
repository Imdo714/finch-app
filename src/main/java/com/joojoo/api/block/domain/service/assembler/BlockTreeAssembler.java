package com.joojoo.api.block.domain.service.assembler;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponse;
import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponseDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BlockTreeAssembler {

    /** 리스트형식에서 응답 형식인 트리구조로 변환 */
    public BlockResponse assembleReconstructBlockTree(List<Block> allBlocks) {
        Map<Long, BlockResponseDto> dtoMap = getBlockResponseDtoMap(allBlocks);
        connectNodes(allBlocks, dtoMap);
        return BlockResponse.of(getRootBlocks(allBlocks, dtoMap));
    }

    /** BlockResponseDto 전용 맵 생성 */
    private static Map<Long, BlockResponseDto> getBlockResponseDtoMap(List<Block> allBlocks) {
        return allBlocks.stream()
                .map(block -> BlockResponseDto.of(block, new ArrayList<>()))
                .collect(Collectors.toMap(BlockResponseDto::getBlockId, dto -> dto));
    }

    /** 부모-자식 관계 연결 담당하는 전용 메서드 */
    private void connectNodes(List<Block> allBlocks, Map<Long, BlockResponseDto> dtoMap) {
        for (Block block : allBlocks) {
            if (block.getParent() != null) { // 부모 블럭이 아니면
                // 부모 블럭을 찾는다.
                BlockResponseDto parentDto = dtoMap.get(block.getParent().getId());
                if (parentDto != null) { // 부모 블럭이 있다면
                    // 부모블럭에 현재 자식 블럭을 추가한다.
                    parentDto.getChildren().add(dtoMap.get(block.getId()));
                }
            }
        }
    }

    /** Root 블럭만 추출해 리스트로 변환 */
    private static List<BlockResponseDto> getRootBlocks(List<Block> allBlocks, Map<Long, BlockResponseDto> dtoMap) {
        return allBlocks.stream()
                .filter(b -> b.getParent() == null)
                .map(b -> dtoMap.get(b.getId()))
                .toList();
    }
}
