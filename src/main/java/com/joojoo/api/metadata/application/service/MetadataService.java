package com.joojoo.api.metadata.application.service;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.presentation.dto.request.metadata.MatchedMetadataDto;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.common.domain.enums.TagSourceType;
import com.joojoo.api.metadata.application.port.in.MetadataUseCase;
import com.joojoo.api.metadata.application.port.out.MetadataPort;
import com.joojoo.api.metadata.domain.MetadataContext;
import com.joojoo.api.metadata.domain.service.MetadataAnalyzer;
import com.joojoo.api.tag.application.in.TagInService;
import com.joojoo.api.ticker.application.in.TickerInService;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MetadataService implements MetadataUseCase {

    private final MetadataAnalyzer metadataAnalyzer;
    private final MetadataPort metadataPort;

    private final TagInService tagInService;
    private final TickerInService tickerInService;

    @Override
    @Transactional
    public void processMetadata(List<Block> blocks, Long userId) {
        if (blocks == null || blocks.isEmpty()) return;

        MetadataContext context = new MetadataContext();
        Map<Block, List<MatchedMetadataDto>> analysisMap = new HashMap<>();

        // 티커, 태그 이름 추출 및 수집
        for (Block block : blocks) {
            analysisMap.put(block, metadataAnalyzer.scan(block.getContent(), context));
        }

        // 마스터 데이터(Ticker, Tag) 일괄 로드
        context.loadEntities(tickerInService, tagInService);

        // 도메인 엔티티 생성
        List<BlockTicker> bTickers = metadataAnalyzer.createBlockTickers(analysisMap, context, userId);
        List<BlockTag> bTags = metadataAnalyzer.createBlockTags(analysisMap, context, userId);
        
        // 저장 및 동기화
        metadataPort.saveTickersAndTags(bTickers, bTags);

        // Redis 저장
        if (!context.getTagNames().isEmpty()) {
            metadataPort.syncUserTagsToRedis(userId, context.getTagMap());
            metadataPort.syncUserTickersToRedis(userId, context.getTickerMap());
        }
    }

    @Override
    @Transactional
    public void processMetadata(Block targetBlock, Long userId) {
        List<BlockTag> oldTags = metadataPort.findAllTagsByBlockId(targetBlock.getId()); // 메서드명 변경

        // 연관된 티커 태그 삭제
        metadataPort.deleteMetadataByBlockId(targetBlock.getId());

        if (!oldTags.isEmpty()) {
            metadataPort.processRedisTagRemoval(userId, oldTags);
        }
        this.processMetadata(Collections.singletonList(targetBlock), userId);
    }

    @Override
    @Transactional
    public void processTradeLogMetadata(TradeLog tradeLog, Long userId, Ticker mainTicker) {
        MetadataContext context = new MetadataContext();

        // 분석 대상 데이터 정의 (SourceType별 콘텐츠 스캔)
        Map<TagSourceType, String> sources = tradeLog.getMetadataSources();

        // 스캔 및 마스터 데이터 일괄 로드
        Map<TagSourceType, List<MatchedMetadataDto>> analysisMap = metadataAnalyzer.scanSources(sources, context);
        context.loadEntities(tickerInService, tagInService);

        // 도메인 엔티티 생성
        List<BlockTicker> tlTickers = metadataAnalyzer.createTradeLogTickers(analysisMap, context, tradeLog, userId);
        List<BlockTag> tlTags = metadataAnalyzer.createTradeLogTags(analysisMap, context, tradeLog, userId);
        tlTickers.add(BlockTicker.create(tradeLog, mainTicker, userId, TagSourceType.TRADE_HEADER, -1, 0));

        // 저장 및 동기화
        metadataPort.saveTickersAndTags(tlTickers, tlTags);

        // Redis 저장
        if (!context.getTagNames().isEmpty()) {
            metadataPort.syncUserTagsToRedis(userId, context.getTagMap());
            metadataPort.syncUserTickersToRedis(userId, context.getTickerMap());
        }
    }

}
