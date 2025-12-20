package com.joojoo.api.block.application;

import com.joojoo.api.block.application.metadata.MetadataService;
import com.joojoo.api.block.application.validate.blockerTree.BlockTreeValidator;
import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockRequestDto;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockSaveRequestDto;
import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponse;
import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponseDto;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.global.exception.handleException.users.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService {

    private final BlockRepository blockRepository;
    private final UserRepository userRepository;
    private final BlockTreeValidator blockTreeValidator;
    private final MetadataService metadataService;

    @Override
    @Transactional
    public BlockResponse saveBlockTree(Long userId, BlockSaveRequestDto requestDto) {
        blockTreeValidator.validateStructure(requestDto);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        // 블록 엔티티 저장
        List<Block> allBlocks = createAndSaveBlocks(user, requestDto.getBlocks());
        BlockResponse blockResponse = reconstructBlockTree(allBlocks);

        metadataService.processMetadata(allBlocks);
        return blockResponse;
    }

    /** 리스트에 블럭을 담아 한번에 저장하는 메서드 */
    private List<Block> createAndSaveBlocks(User user, List<BlockRequestDto> dtos) {
        List<Block> allBlocks = new ArrayList<>();
        blocksRecursive(user, null, dtos, allBlocks);
        return blockRepository.saveAll(allBlocks); // TODO : JDBC Batch Insert 고려, 지금 블럭이 10개면 10개의 Insert 쿼리 작동 중
    }

    /** List에서 다시 트리 형식으로 변환 */
    private BlockResponse reconstructBlockTree(List<Block> blocks) {
        // ID를 키로 하는 DTO Map 생성
        Map<Long, BlockResponseDto> dtoMap = blocks.stream()
                .map(block -> BlockResponseDto.of(block, new ArrayList<>()))
                .collect(Collectors.toMap(BlockResponseDto::getBlockId, dto -> dto));

        // 부모-자식 연결 및 루트 블록 추출
        List<BlockResponseDto> rootBlocks = new ArrayList<>();
        for (Block block : blocks) {
            BlockResponseDto currentDto = dtoMap.get(block.getId());

            if (block.getParent() == null) {
                rootBlocks.add(currentDto);
            } else {
                BlockResponseDto parentDto = dtoMap.get(block.getParent().getId());
                if (parentDto != null) {
                    parentDto.getChildren().add(currentDto);
                }
            }
        }
        return BlockResponse.of(rootBlocks);
    }

    /** RequestDto를 순회하며 JPA 엔티티(Block)를 생성하고, allBlocks에 수집하는 메서드 */
    private void blocksRecursive(User user, Block parentBlock, List<BlockRequestDto> blockDtos, List<Block> allBlocks) {
        if (blockDtos == null || blockDtos.isEmpty()) { return; }

        for (BlockRequestDto dto : blockDtos) {
            Block block = Block.createBlockBuild(user, parentBlock, dto);
            allBlocks.add(block);
            blocksRecursive(user, block, dto.getChildren(), allBlocks); // 자식 재귀 호출
        }
    }

}
