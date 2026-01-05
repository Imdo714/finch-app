package com.joojoo.api.metadata.domain.service;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.presentation.dto.request.metadata.MatchedMetadataDto;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.api.metadata.domain.MetadataContext;
import com.joojoo.global.common.enums.TagSourceType;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MetadataAnalyzer { /** 정규식으로 이름들을 추출하고 반환하는 "순수 로직" */

    private static final Pattern COMBINED_PATTERN = Pattern.compile("\\$([a-zA-Z0-9가-힣.-]+)|#([^\\s#]+)");

    /** 텍스트에서 메타데이터 추출 및 컨텍스트에 이름 수집 */
    public List<MatchedMetadataDto> scan(String content, MetadataContext context) {
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

    /** 분석된 결과를 바탕으로 BlockTicker 엔티티 리스트를 생성 */
    public List<BlockTicker> createBlockTickers(
            Map<Block, List<MatchedMetadataDto>> analysisMap,
            MetadataContext context,
            Long userId
    ) {
        List<BlockTicker> tickers = new ArrayList<>();
        analysisMap.forEach((block, matches) -> {
            int tSeq = 0;
            for (MatchedMetadataDto match : matches) {
                if (match.isTicker()) {
                    Ticker ticker = context.getTicker(match.name());
                    if (ticker != null) {
                        tickers.add(BlockTicker.create(block, ticker, userId, match.start(), tSeq++, TagSourceType.BLOCK_CONTENT));
                    }
                }
            }
        });
        return tickers;
    }

    /** 분석된 결과를 바탕으로 BlockTag 엔티티 리스트를 생성 */
    public List<BlockTag> createBlockTags(
            Map<Block, List<MatchedMetadataDto>> analysisMap,
            MetadataContext context,
            Long userId
    ) {
        List<BlockTag> tags = new ArrayList<>();
        analysisMap.forEach((block, matches) -> {
            int tagSeq = 0;
            for (MatchedMetadataDto match : matches) {
                if (!match.isTicker()) {
                    Tag tag = context.getTag(match.name());
                    if (tag != null) {
                        tags.add(BlockTag.create(block, tag, userId, match.start(), tagSeq++, TagSourceType.BLOCK_CONTENT));
                    }
                }
            }
        });
        return tags;
    }

    /**
     * 전달받은 각 소스(메모, 리스크 등)를 순회하며 텍스트 내 티커 및 태그를 스캔하고
     * 그 결과를 소스 타입별로 그룹화하여 매핑합니다.
     */
    public Map<TagSourceType, List<MatchedMetadataDto>> scanSources(Map<TagSourceType, String> sources, MetadataContext context) {
        Map<TagSourceType, List<MatchedMetadataDto>> analysisMap = new HashMap<>();

        sources.forEach((type, content) -> {
            if (content != null && !content.isBlank()) {
                analysisMap.put(type, this.scan(content, context));
            }
        });

        return analysisMap;
    }

    /** 분석된 결과를 바탕으로 TradeLog용 BlockTicker 리스트 생성 */
    public List<BlockTicker> createTradeLogTickers(
            Map<TagSourceType, List<MatchedMetadataDto>> analysisMap,
            MetadataContext context,
            TradeLog tradeLog,
            Long userId
    ) {
        List<BlockTicker> tickers = new ArrayList<>();
        analysisMap.forEach((type, matches) -> {
            int tSeq = 0;
            for (MatchedMetadataDto match : matches) {
                if (match.isTicker()) {
                    Ticker ticker = context.getTicker(match.name());
                    if (ticker != null) {
                        tickers.add(BlockTicker.create(tradeLog, ticker, userId, type, match.start(), tSeq++));
                    }
                }
            }
        });
        return tickers;
    }

    /** 분석된 결과를 바탕으로 TradeLog용 BlockTag 리스트 생성 */
    public List<BlockTag> createTradeLogTags(
            Map<TagSourceType, List<MatchedMetadataDto>> analysisMap,
            MetadataContext context,
            TradeLog tradeLog,
            Long userId
    ) {
        List<BlockTag> tags = new ArrayList<>();
        analysisMap.forEach((type, matches) -> {
            int tagSeq = 0;
            for (MatchedMetadataDto match : matches) {
                if (!match.isTicker()) {
                    Tag tag = context.getTag(match.name());
                    if (tag != null) {
                        tags.add(BlockTag.create(tradeLog, tag, userId, type, match.start(), tagSeq++));
                    }
                }
            }
        });
        return tags;
    }

}
