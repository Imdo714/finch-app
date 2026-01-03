package com.joojoo.api.block.application.service.query;

import com.joojoo.api.block.application.port.in.GetBlockUseCase;
import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.block.domain.service.assembler.BlockTreeAssembler;
import com.joojoo.api.block.domain.service.validation.BlockDateValidator;
import com.joojoo.api.block.presentation.dto.response.detail.BlockDetailResponseDto;
import com.joojoo.api.block.presentation.dto.response.mainView.BlockMainViewResponse;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import com.joojoo.global.exception.handleException.block.BlockNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockQueryService implements GetBlockUseCase {

    private final BlockRepository blockRepository;
    private final BlockTagRepository blockTagRepository;
    private final BlockTickerRepository blockTickerRepository;

    private final BlockDateValidator blockDateValidator;
    private final BlockTreeAssembler blockTreeAssembler;

    @Override
    public BlockMainViewResponse getBlockMainView(Long userId, LocalDate lastDate) {
        LocalDate targetDate = blockDateValidator.validateAndGetTargetDate(lastDate);

        // 데이터 조회
        List<Block> rootBlocks = blockRepository.findBlocksByLatestDates(userId, targetDate, 2);
        if (rootBlocks.isEmpty()) {
            return new BlockMainViewResponse(Collections.emptyList(), null);
        }

        List<Long> rootIds = rootBlocks.stream().map(Block::getId).toList();

        // 연관 데이터 조회
        List<BlockTag> tags = blockTagRepository.findAllBlockTags(rootIds);
        List<BlockTicker> tickers = blockTickerRepository.findAllBlockTickers(rootIds);
        Map<Long, Long> childCounts = blockRepository.getChildCounts(rootIds);

        // 응답 값 조립
        List<BlockDetailResponseDto> allDtos = blockTreeAssembler.assembleMainList(rootBlocks, tags, tickers, childCounts);

        LocalDate oldestDateInResult = allDtos.get(allDtos.size() - 1).getCreatedAt().toLocalDate();
        LocalDate nextDate = blockRepository.findNextAvailableDate(userId, oldestDateInResult);

        return BlockMainViewResponse.of(allDtos, nextDate);
    }

    @Override
    public BlockDetailResponseDto getBlockDetail(Long rootId) {
        List<Block> blocks = blockRepository.findAllChildrenByRootId(rootId);
        if (blocks.isEmpty()) throw new BlockNotFoundException();

        List<Long> ids = blocks.stream().map(Block::getId).toList();
        List<BlockTag> tags = blockTagRepository.findAllBlockTags(ids);
        List<BlockTicker> tickers = blockTickerRepository.findAllBlockTickers(ids);

        return blockTreeAssembler.assembleDetailTree(rootId, blocks, tags, tickers);
    }

}
