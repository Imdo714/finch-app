package com.joojoo.api.block.application;

import com.joojoo.api.block.application.validate.blockerTree.BlockTreeValidator;
import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockRequestDto;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockSaveRequestDto;
import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponse;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.global.exception.handleException.users.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService {

    private final BlockRepository blockRepository;
    private final UserRepository userRepository;
    private final BlockTreeValidator blockTreeValidator;

    @Override
    @Transactional
    public BlockResponse saveBlockTree(Long userId, BlockSaveRequestDto requestDto) {
        blockTreeValidator.validateStructure(requestDto);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        // 벌크 처리를 위한 Block 저장소
        List<Block> allBlocks = new ArrayList<>();
        blocksRecursive(user, null, requestDto.getBlocks(), allBlocks);
        // TODO : JDBC Batch Insert 고려, 지금 블럭이 10개면 10개의 Insert 쿼리 작동 중
        blockRepository.saveAll(allBlocks);
        log.info("allBlocks = {}", allBlocks);

        return BlockResponse.of(null);
    }

    /** RequestDto를 순회하며 JPA 엔티티(Block)를 생성하고, allBlocks에 수집하는 메서드 */
    private void blocksRecursive(User user, Block parentBlock, List<BlockRequestDto> blockDtos, List<Block> allBlocks) {
        if (blockDtos == null || blockDtos.isEmpty()) { return; }

        for (BlockRequestDto dto : blockDtos) {
            Block block = Block.createBlockBuild(user, parentBlock, dto);
            allBlocks.add(block); // 벌크 처리할려고 담기
            blocksRecursive(user, block, dto.getChildren(), allBlocks); // 자식 재귀 호출
        }
    }

}
