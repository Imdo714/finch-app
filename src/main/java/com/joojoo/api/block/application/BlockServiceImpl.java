package com.joojoo.api.block.application;

import com.joojoo.api.block.application.detail.BlockDtoAssembler;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.util.metadata.MetadataService;
import com.joojoo.api.block.application.validate.blockerTree.BlockTreeValidator;
import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.model.enums.DeleteMode;
import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockRequestDto;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockSaveRequestDto;
import com.joojoo.api.block.presentation.dto.request.updateBlock.BlockUpdateDto;
import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponse;
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
import com.joojoo.global.util.HangulUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
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
        metadataService.processMetadata(allBlocks, user.getId());
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

    @Override
    @Transactional
    public void deleteBlock(Long userId, Long blockId, DeleteMode mode) {
        Block targetBlock = blockRepository.findByIdWithChildren(blockId)
                .orElseThrow(BlockNotFoundException::new);

        blockTreeValidator.validateOwner(targetBlock, userId);

        /** 삭제할 블럭 ID의 태그를 미리 수집 */
        List<Long> idsToDelete = new ArrayList<>();
        if (mode == DeleteMode.ALL) {
            targetBlock.collectAllIds(idsToDelete);
        } else {
            idsToDelete.add(targetBlock.getId());
        }

        List<BlockTag> tagsToRemove = blockTagRepository.findAllByBlockIdIn(idsToDelete);

        if (mode == DeleteMode.ALL) {
            handleRecursiveDelete(targetBlock, idsToDelete);
        } else {
            handleSingleDeleteWithPromotion(targetBlock);
        }

        if (!tagsToRemove.isEmpty()) {
            processRedisTagRemoval(userId, tagsToRemove);
        }
    }

    @Override
    @Transactional
    public void updateBlock(Long userId, Long blockId, BlockUpdateDto blockUpdateDto) {
        Block targetBlock = blockRepository.findByIdWithChildren(blockId)
                .orElseThrow(BlockNotFoundException::new);
        blockTreeValidator.validateOwner(targetBlock, userId);

        targetBlock.updateContent(blockUpdateDto.getContent());
        metadataService.processMetadata(targetBlock, userId);
    }

    /** Redis에 있는 태그 -1 또는 삭제 */
    private void processRedisTagRemoval(Long userId, List<BlockTag> tagsToRemove) {
        Map<Long, List<BlockTag>> groupedTags = tagsToRemove.stream()
                .collect(Collectors.groupingBy(bt -> bt.getTag().getId()));

        groupedTags.forEach((tagId, tags) -> {
            Tag tag = tags.get(0).getTag();
            String name = tag.getName();
            int countToRemove = tags.size();

            Set<String> lexEntries = new HashSet<>();
            lexEntries.add(userId + ":" + HangulUtils.splitToJaso(name) + "*" + name + "*" + tagId);
            lexEntries.add(userId + ":" + HangulUtils.getChosung(name) + "*" + name + "*" + tagId);

            // Repository 호출 (태그 하나당 한 번씩 호출하거나, Map 자체를 넘기도록 수정)
            blockTagRepository.removeTagsFromRedis(userId, tagId, lexEntries, countToRemove);
        });
    }

    /** 자식들 시퀀스 앞으로 댕기고 삭제 */
    private void handleRecursiveDelete(Block targetBlock, List<Long> idsToDelete) {
        shiftSiblings(targetBlock, -1);
        blockRepository.deleteAllByIdInBatch(idsToDelete);
    }

    /** 블럭 정보를 수정한 후 연관관계 삭제 */
    private void handleSingleDeleteWithPromotion(Block targetBlock) {
        Block parentBlock = targetBlock.getParent();
        List<Block> children = new ArrayList<>(targetBlock.getChildren());
        blockTreeValidator.validatePromotionLimit(targetBlock, parentBlock);

        // 시퀀스 공간 확보
        int offset = children.size() - 1;
        shiftSiblings(targetBlock, offset);

        // 수정된 부모Id 일괄 수정, 그런데 영속성 컨텍스트의 객체들은 여전히 이전 부모를 가리킴
        blockRepository.updateChildrenParent(targetBlock, parentBlock);

        // 자식 및 모든 자손의 뎁스/시퀀스 조정, 벌크 연산은 메모리 정보를 수정되지 않아 Dirty Checking 해줘야 함
        int startSeq = targetBlock.getSequence();
        for (int i = 0; i < children.size(); i++) {
            children.get(i).promote(parentBlock, startSeq + i);
        }

        targetBlock.disconnectChildren();
        blockRepository.delete(targetBlock);
    }

    /** 시퀀스 조정 메서드 */
    private void shiftSiblings(Block targetBlock, int offset) {
        if (offset == 0) return;
        if (targetBlock.getParent() != null) {
            blockRepository.updateSequenceWithParent(targetBlock.getUser(), targetBlock.getParent(), targetBlock.getSequence(), offset);
        } else {
            blockRepository.updateSequenceRoot(targetBlock.getUser(), targetBlock.getSequence(), offset);
        }
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
