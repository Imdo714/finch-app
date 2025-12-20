package com.joojoo.api.block.application;

import com.joojoo.api.block.application.detail.BlockDtoAssembler;
import com.joojoo.api.block.application.metadata.MetadataService;
import com.joojoo.api.block.application.validate.blockerTree.BlockTreeValidator;
import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockRequestDto;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockSaveRequestDto;
import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponse;
import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponseDto;
import com.joojoo.api.block.presentation.dto.response.detail.BlockDetailResponseDto;
import com.joojoo.api.block.presentation.dto.response.detail.BlockMainViewResponseDto;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.global.exception.handleException.block.BlockNotFoundException;
import com.joojoo.global.exception.handleException.users.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
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

    private final BlockDtoAssembler blockDtoAssembler;
    private final BlockTagRepository blockTagRepository;
    private final BlockTickerRepository blockTickerRepository;

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

    @Override
    @Transactional(readOnly = true)
    public BlockDetailResponseDto getBlockDetail(Long rootId) {
        List<Block> blocks = blockRepository.findAllChildrenByRootId(rootId);
        if (blocks.isEmpty()) throw new BlockNotFoundException();

        List<Long> ids = blocks.stream().map(Block::getId).toList();
        List<BlockTag> tags = blockTagRepository.findAllBlockTags(ids);
        List<BlockTicker> tickers = blockTickerRepository.findAllBlockTickers(ids);

        return blockDtoAssembler.assembleTree(rootId, blocks, tags, tickers);
    }

    @Override
    public BlockMainViewResponseDto getBlockMainView(Long userId, Long lastId, int size) {
        List<Block> rootBlocks = blockRepository.findRootBlocks(userId, lastId, size);
        if (rootBlocks.isEmpty()) return null;

        List<Long> rootIds = rootBlocks.stream().map(Block::getId).toList();
        List<BlockTag> tags = blockTagRepository.findAllBlockTags(rootIds);
        List<BlockTicker> tickers = blockTickerRepository.findAllBlockTickers(rootIds);

        Map<Long, Long> childCounts = blockRepository.getChildCounts(rootIds);

        Map<Long, List<BlockDetailResponseDto.MetadataResponse>> tagMap = tags.stream()
                .collect(Collectors.groupingBy(
                        bt -> bt.getBlock().getId(),
                        Collectors.mapping(bt -> BlockDetailResponseDto.MetadataResponse.of(
                                bt.getTag().getId(),
                                bt.getTag().getName(),
                                bt.getSequence(),
                                bt.getStartOffset()), Collectors.toList())
                ));

        Map<Long, List<BlockDetailResponseDto.MetadataResponse>> tickerMap = tickers.stream()
                .collect(Collectors.groupingBy(
                        bt -> bt.getBlock().getId(),
                        Collectors.mapping(bt -> BlockDetailResponseDto.MetadataResponse.of(
                                bt.getTicker().getId(),
                                bt.getTicker().getName(),
                                bt.getSequence(),
                                bt.getStartOffset()), Collectors.toList())
                ));

        List<BlockDetailResponseDto> blockList = rootBlocks.stream()
                .map(block -> BlockDetailResponseDto.fromSummary(
                        block,
                        tagMap.getOrDefault(block.getId(), Collections.emptyList()),
                        tickerMap.getOrDefault(block.getId(), Collections.emptyList()),
                        childCounts.getOrDefault(block.getId(), 0L)
                ))
                .toList();

        return BlockMainViewResponseDto.of(blockList);
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
