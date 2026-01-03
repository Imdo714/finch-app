package com.joojoo.api.metadata.application.service;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.presentation.dto.request.metadata.MatchedMetadataDto;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.metadata.application.port.in.MetadataUseCase;
import com.joojoo.api.metadata.application.port.out.MetadataPort;
import com.joojoo.api.metadata.domain.service.MetadataAnalyzer;
import com.joojoo.api.tag.application.in.TagInService;
import com.joojoo.api.ticker.application.in.TickerInService;
import com.joojoo.api.util.metadata.MetadataContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MetadataUseCaseService implements MetadataUseCase {

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
        }
    }

}
