package com.joojoo.api.util.metadata;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.presentation.dto.request.metadata.MatchedMetadataDto;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import com.joojoo.api.tag.application.in.TagInService;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.ticker.application.in.TickerInService;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MetadataServiceImpl implements MetadataService {

    private static final Pattern COMBINED_PATTERN = Pattern.compile("\\$([a-zA-Z0-9가-힣.-]+)|#([^\\s#]+)");

    private final TagInService tagInService;
    private final TickerInService tickerInService;

    private final BlockTickerRepository blockTickerRepository;
    private final BlockTagRepository blockTagRepository;

    @Override
    public void processMetadata(List<Block> blocks, Long userId) {
        if (blocks == null || blocks.isEmpty()) return;

        MetadataContext context = new MetadataContext();
        Map<Block, List<MatchedMetadataDto>> analysisMap = new HashMap<>();

        // 1. 이름 추출 및 수집
        for (Block block : blocks) {
            analysisMap.put(block, scanContent(block.getContent(), context));
        }

        // 마스터 데이터(Ticker, Tag) 일괄 로드
        context.loadEntities(tickerInService, tagInService);

        List<BlockTicker> bTickers = new ArrayList<>();
        List<BlockTag> bTags = new ArrayList<>();

        analysisMap.forEach((block, matches) -> {
            int tSeq = 0, tagSeq = 0;
            for (MatchedMetadataDto match : matches) {
                if (match.isTicker()) {
                    Ticker ticker = context.getTicker(match.name());
                    if (ticker != null) {
                        bTickers.add(BlockTicker.create(block, ticker, userId, match.start(), tSeq++));
                    }
                } else {
                    Tag tag = context.getTag(match.name());
                    if (tag != null) {
                        bTags.add(BlockTag.create(block, tag, userId, match.start(), tagSeq++));
                    }
                }
            }
        });

        if (!bTickers.isEmpty()) blockTickerRepository.saveAll(bTickers);
        if (!bTags.isEmpty()) blockTagRepository.saveAll(bTags);
    }

    /** 단일 블록 업데이트용 */
    @Override
    public void processMetadata(Block targetBlock, Long userId) {
        blockTickerRepository.deleteByBlockIds(targetBlock.getId());
        blockTagRepository.deleteByBlockIds(targetBlock.getId());
        this.processMetadata(Collections.singletonList(targetBlock), userId);
    }

//    @Override
//    public void processTradeLogMetadata(TradeLog tradeLog, Long userId) {
//        MetadataContext context = new MetadataContext();
//
//        Map<String, String> sources = new LinkedHashMap<>();
//        sources.put("MEMO", tradeLog.getMemo());
//        sources.put("RISK", tradeLog.getRiskFactor());
//        sources.put("PLAN", tradeLog.getTradingPlan());
//
//        Map<String, List<MatchedMetadataDto>> analysisResults = new HashMap<>();
//        sources.forEach((field, content) -> {
//            if (content != null) {
//                analysisResults.put(field, scanContent(content, context));
//            }
//        });
//
//        context.loadEntities(tickerInService, tagInService);
//
//        List<TradeLogTicker> tlTickers = new ArrayList<>();
//        List<TradeLogTag> tlTags = new ArrayList<>();
//
//        analysisResults.forEach((field, matches) -> {
//            int tSeq = 0, tagSeq = 0;
//            for (MatchedMetadataDto match : matches) {
//                if (match.isTicker()) {
//                    Ticker ticker = context.getTicker(match.name());
//                    if (ticker != null) {
//                        tlTickers.add(TradeLogTicker.create(tradeLog, ticker, userId, field, match.start(), tSeq++));
//                    }
//                } else {
//                    Tag tag = context.getTag(match.name());
//                    if (tag != null) {
//                        tlTags.add(TradeLogTag.create(tradeLog, tag, userId, field, match.start(), tagSeq++));
//                    }
//                }
//            }
//        });
//
//        if (!tlTickers.isEmpty()) tradeLogTickerRepository.saveAll(tlTickers);
//        if (!tlTags.isEmpty()) tradeLogTagRepository.saveAll(tlTags);
//    }

    /** 텍스트에서 메타데이터 추출 및 컨텍스트에 이름 수집 */
    private List<MatchedMetadataDto> scanContent(String content, MetadataContext context) {
        if (content == null || content.isEmpty()) return Collections.emptyList();

        List<MatchedMetadataDto> matches = new ArrayList<>();
        Matcher matcher = COMBINED_PATTERN.matcher(content);

        while (matcher.find()) {
            String tickerName = matcher.group(1);
            String tagName = matcher.group(2);

            if (tickerName != null) {
                context.addTicker(tickerName);
                matches.add(new MatchedMetadataDto(tickerName, matcher.start(), true));
            } else if (tagName != null) {
                context.addTag(tagName);
                matches.add(new MatchedMetadataDto(tagName, matcher.start(), false));
            }
        }
        return matches;
    }
}
