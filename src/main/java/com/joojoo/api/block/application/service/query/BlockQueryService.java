package com.joojoo.api.block.application.service.query;

import com.joojoo.api.block.application.port.in.GetBlockUseCase;
import com.joojoo.api.block.application.port.out.LoadDailyDetailsPort;
import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.block.domain.service.assembler.BlockTreeAssembler;
import com.joojoo.api.block.domain.service.date.BlockDatePolicy;
import com.joojoo.api.block.presentation.dto.response.detail.BlockDetailResponseDto;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import com.joojoo.api.common.assembler.detailApiAssembler.DetailResponseAssembler;
import com.joojoo.api.common.date.DateUtils;
import com.joojoo.api.common.domain.response.detail.DailyBlockDetailsResponse;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.global.exception.handleException.block.BlockNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockQueryService implements GetBlockUseCase {

    private final BlockRepository blockRepository;
    private final BlockTagRepository blockTagRepository;
    private final BlockTickerRepository blockTickerRepository;
    private final BlockDatePolicy blockDatePolicy;
    private final DetailResponseAssembler detailResponseAssembler;
    private final DateUtils dateUtils;
    private final BlockTreeAssembler blockTreeAssembler;
    private final LoadDailyDetailsPort loadDailyDetailsPort;


    @Override
    public DailyBlockDetailsResponse getBlockMainView(Long userId, LocalDate lastDate) {
        LocalDate targetDate = dateUtils.validateAndGetTargetDate(lastDate);

        // 최종 3일치 확정
        List<LocalDate> finalTargetDates = blockDatePolicy.finalTargetDates(userId, targetDate);

        // 날짜 분리 상위 2일치 만 조회
        List<LocalDate> displayDates = blockDatePolicy.extractDisplayDates(finalTargetDates); 

        // 데이터 2일치만 조회
        List<Block> rootBlocks = loadDailyDetailsPort.findBlocksByDates(userId, displayDates);
        List<TradeLog> rootTradeLog = loadDailyDetailsPort.findTradeLogsByDates(userId, displayDates);

        return detailResponseAssembler.assembleTagsAndTickersDetailResponse(finalTargetDates, rootBlocks, rootTradeLog);
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
