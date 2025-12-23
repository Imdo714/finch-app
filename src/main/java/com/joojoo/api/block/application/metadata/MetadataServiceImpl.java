package com.joojoo.api.block.application.metadata;

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
import java.util.concurrent.atomic.AtomicInteger;
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

    /** Block.content을 읽어 Ticker, Tag를 찾아서 담기 */
    @Override
    public Map<Block, List<MatchedMetadataDto>> scanBlocks(List<Block> blocks, Set<String> tickerNames, Set<String> tagNames) {
        Map<Block, List<MatchedMetadataDto>> analysisResult = new HashMap<>();

        for (Block block : blocks) {
            String content = block.getContent();
            if (content == null || content.isEmpty()) continue;

            List<MatchedMetadataDto> matches = new ArrayList<>();
            Matcher matcher = COMBINED_PATTERN.matcher(content);

            while (matcher.find()) {
                if (matcher.group(1) != null) { // 티커
                    String name = matcher.group(1);
                    tickerNames.add(name);
                    matches.add(new MatchedMetadataDto(name, matcher.start(), true));
                } else if (matcher.group(2) != null) { // 태그
                    String name = matcher.group(2);
                    tagNames.add(name);
                    matches.add(new MatchedMetadataDto(name, matcher.start(), false));
                }
            }
            analysisResult.put(block, matches);
        }
        return analysisResult;
    }

    @Override
    public void processMetadata(List<Block> blocks, Long userId) {
        // 정규식 메타데이버 검사
        Set<String> tickerNames = new HashSet<>();
        Set<String> tagNames = new HashSet<>();
        Map<Block, List<MatchedMetadataDto>> analysisMap = scanBlocks(blocks, tickerNames, tagNames);

        // Ticker, Tag Entity 준비
        Map<String, Ticker> tickerMap = tickerInService.getTickerMap(tickerNames);
        Map<String, Tag> tagMap = tagInService.getOrCreateTagMap(tagNames);

        // 연관관계 엔티티 생성
        List<BlockTicker> blockTickers = new ArrayList<>();
        List<BlockTag> blockTags = new ArrayList<>();

        for (Block block : blocks) {
            List<MatchedMetadataDto> matches = analysisMap.getOrDefault(block, Collections.emptyList());
            mapToEntities(block, userId, matches, tickerMap, tagMap, blockTickers, blockTags);
        }

        // TODO : JDBC Batch Insert 고려, 지금 중간 테이블이 10개면 10개의 Insert 쿼리 작동 중
        blockTickerRepository.saveAll(blockTickers);
        blockTagRepository.saveAll(blockTags);
    }

    /** 추출된 맵을 바탕으로 BlockTicker, BlockTag 중간 테이블 엔티티 생성 */
    public void mapToEntities(Block block, Long userId, List<MatchedMetadataDto> matches, Map<String, Ticker> tickerMap, Map<String, Tag> tagMap, List<BlockTicker> bTickers, List<BlockTag> bTags) {
        if (block.getContent() == null) return;

        AtomicInteger tSeq = new AtomicInteger();
        AtomicInteger tagSeq = new AtomicInteger();

        for (MatchedMetadataDto match : matches) {
            if (match.isTicker()) {
                Optional.ofNullable(tickerMap.get(match.name()))
                        .ifPresent(t -> bTickers.add(BlockTicker.create(block, t, userId, match.start(), tSeq.getAndIncrement())));
            } else {
                Optional.ofNullable(tagMap.get(match.name()))
                        .ifPresent(tag -> bTags.add(BlockTag.create(block, tag, userId, match.start(), tagSeq.getAndIncrement())));
            }
        }
    }




}
