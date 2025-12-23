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
import com.joojoo.api.block.presentation.dto.response.mainView.BlockMainViewResponse;
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

import java.time.LocalDate;
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

        List<Block> allBlocks = createAndSaveBlocks(user, requestDto.getBlocks());
        metadataService.processMetadata(allBlocks);
        return blockDtoAssembler.assembleReconstructBlockTree(allBlocks);
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
    @Transactional(readOnly = true)
    public BlockMainViewResponse getBlockMainView(Long userId, LocalDate lastDate) {
        LocalDate targetDate = blockTreeValidator.validateAndGetTargetDate(lastDate);

        List<Block> rootBlocks = blockRepository.findBlocksByLatestDates(userId, targetDate, 2);
        if (rootBlocks.isEmpty()) {
            return new BlockMainViewResponse(Collections.emptyList(), null);
        }

        List<Long> rootIds = rootBlocks.stream().map(Block::getId).toList();
        List<BlockTag> tags = blockTagRepository.findAllBlockTags(rootIds);
        List<BlockTicker> tickers = blockTickerRepository.findAllBlockTickers(rootIds);
        Map<Long, Long> childCounts = blockRepository.getChildCounts(rootIds);

        List<BlockDetailResponseDto> allDtos = blockDtoAssembler.assembleMainList(rootBlocks, tags, tickers, childCounts);
        LocalDate oldestDateInResult = allDtos.get(allDtos.size() - 1).getCreatedAt().toLocalDate();
        LocalDate nextDate = blockRepository.findNextAvailableDate(userId, oldestDateInResult);

        return BlockMainViewResponse.of(allDtos, nextDate);
    }

    /** 리스트에 블럭을 담아 한번에 저장하는 메서드 */
    private List<Block> createAndSaveBlocks(User user, List<BlockRequestDto> dtos) {
        List<Block> allBlocks = new ArrayList<>();
        blocksRecursive(user, null, dtos, allBlocks);
        return blockRepository.saveAll(allBlocks); // TODO : JDBC Batch Insert 고려, 지금 블럭이 10개면 10개의 Insert 쿼리 작동 중
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
