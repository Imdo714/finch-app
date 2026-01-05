package com.joojoo.api.util.metadata;

import com.joojoo.api.block.presentation.dto.request.metadata.MatchedMetadataDto;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import com.joojoo.api.tag.application.in.TagInService;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.ticker.application.in.TickerInService;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.global.common.enums.TagSourceType;
import com.joojoo.global.util.HangulUtils;
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
    public void processTradeLogMetadata(TradeLog tradeLog, Long userId, Ticker mainTicker) {
        MetadataContext context = new MetadataContext();

        // 1. 소스 필드와 Enum 매핑
        Map<TagSourceType, String> sources = new LinkedHashMap<>();
        sources.put(TagSourceType.TRADE_MEMO, tradeLog.getMemo());
        sources.put(TagSourceType.TRADE_RISK, tradeLog.getRiskFactor());
        sources.put(TagSourceType.TRADE_PLAN, tradeLog.getTradingPlan());

        // 2. 스캔 결과 수집
        Map<TagSourceType, List<MatchedMetadataDto>> analysisResults = new HashMap<>();
        sources.forEach((type, content) -> {
            if (content != null && !content.isBlank()) {
                analysisResults.put(type, scanContent(content, context));
            }
        });

        context.loadEntities(tickerInService, tagInService);

        List<BlockTicker> tlTickers = new ArrayList<>();
        List<BlockTag> tlTags = new ArrayList<>();

        tlTickers.add(BlockTicker.create(
                tradeLog,
                mainTicker,
                userId,
                TagSourceType.TRADE_HEADER,
                -1, // 본문 내 위치가 아님을 표시
                0
        ));

        // 3. 엔티티 생성
        analysisResults.forEach((type, matches) -> {
            int tSeq = 0, tagSeq = 0;
            for (MatchedMetadataDto match : matches) {
                if (match.isTicker()) {
                    Ticker ticker = context.getTicker(match.name());
                    if (ticker != null) {
                        // tradeLog를 인자로 받는 create 메서드 호출
                        tlTickers.add(BlockTicker.create(tradeLog, ticker, userId, type, match.start(), tSeq++));
                    }
                } else {
                    Tag tag = context.getTag(match.name());
                    if (tag != null) {
                        tlTags.add(BlockTag.create(tradeLog, tag, userId, type, match.start(), tagSeq++));
                    }
                }
            }
        });

        if (!tlTickers.isEmpty()) blockTickerRepository.saveAll(tlTickers);
        if (!tlTags.isEmpty()) blockTagRepository.saveAll(tlTags);

        if (!context.getTagNames().isEmpty()) {
            saveUserTagsToRedis(userId, context.getTagMap());
        }
    }

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

    /** 여러 개의 태그를 한 번에 Redis 포맷으로 변환하여 저장 */
    private void saveUserTagsToRedis(Long userId, Map<String, Tag> tagMap) {
        Set<String> lexEntries = new HashSet<>();
        Set<Long> tagIds = new HashSet<>();

        tagMap.forEach((name, tag) -> {
            Long tagId = tag.getId();

            // 검색용 문자열들 (자소, 초성)
            lexEntries.add(userId + ":" + HangulUtils.splitToJaso(name) + "*" + name + "*" + tagId);
            lexEntries.add(userId + ":" + HangulUtils.getChosung(name) + "*" + name + "*" + tagId);

            // 점수 관리용 ID들
            tagIds.add(tagId);
        });

        blockTagRepository.addTagsToRedis(userId, lexEntries, tagIds);
    }
}
