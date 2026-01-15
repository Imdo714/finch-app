package com.joojoo.api.metadata.infrastructure.persistence;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTag.infrastructure.redis.BlockTagRedisRepository;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import com.joojoo.api.blockTicker.infrastructure.redis.BlockTickerRedisRepository;
import com.joojoo.api.common.hangul.HangulConverter;
import com.joojoo.api.metadata.application.port.out.MetadataPort;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class MetadataPersistenceAdapter implements MetadataPort {

    private final BlockTickerRepository blockTickerRepository;
    private final BlockTickerRedisRepository blockTickerRedisRepository;
    private final BlockTagRepository blockTagRepository;
    private final BlockTagRedisRepository blockTagRedisRepository;
    private final HangulConverter hangulConverter;

    @Override
    public void saveTickersAndTags(List<BlockTicker> tickers, List<BlockTag> tags) {
        if (!tickers.isEmpty()) blockTickerRepository.saveAll(tickers);
        if (!tags.isEmpty()) blockTagRepository.saveAll(tags);
    }

    @Override
    public void syncUserTagsToRedis(Long userId, Map<String, Tag> tagMap) {
        Set<String> lexEntries = new HashSet<>();
        Set<Long> tagIds = new HashSet<>();

        tagMap.forEach((name, tag) -> {
            lexEntries.addAll(generateLexEntries(userId, name, tag.getId()));
            tagIds.add(tag.getId());
        });

        blockTagRedisRepository.addTagsToRedis(userId, lexEntries, tagIds);
    }

    @Override
    public void syncUserTickersToRedis(Long userId, Map<String, Ticker> tickerMap) {
        Set<String> lexEntries = new HashSet<>();
        Set<Long> tickerIds = new HashSet<>();

        tickerMap.forEach((name, ticker) -> {
            lexEntries.addAll(generateSearchKeywords(userId, name, ticker.getId(), ticker.getSymbol()));
            tickerIds.add(ticker.getId());
        });

        blockTickerRedisRepository.addTickersToRedis(userId, lexEntries, tickerIds);
    }

    @Override
    public void processRedisTagRemoval(Long userId, List<BlockTag> oldTags) {
        Map<Tag, Long> tagCounts = oldTags.stream()
                .collect(Collectors.groupingBy(BlockTag::getTag, Collectors.counting()));

        tagCounts.forEach((tag, count) -> {
            Set<String> lexEntries = generateLexEntries(userId, tag.getName(), tag.getId());
            blockTagRedisRepository.removeTagsFromRedis(userId, tag.getId(), lexEntries, count.intValue());
        });
    }

    @Override
    public void processRedisTickerRemoval(Long userId, List<BlockTicker> oldTickers) {
        Map<Ticker, Long> tickerCounts = oldTickers.stream()
                .collect(Collectors.groupingBy(BlockTicker::getTicker, Collectors.counting()));

        tickerCounts.forEach((ticker, count) -> {
            Set<String> lexEntries = generateSearchKeywords(userId, ticker.getName(), ticker.getId(), ticker.getSymbol());
            blockTickerRedisRepository.removeTickersFromRedis(userId, ticker.getId(), lexEntries, count.intValue());
        });
    }

    @Override
    public void deleteMetadataByBlockId(Long blockId) {
        blockTickerRepository.deleteByBlockIds(blockId);
        blockTagRepository.deleteByBlockIds(blockId);
    }

    @Override
    @Transactional
    public void deleteMetadataByTradeLogId(Long tradeLogId, Long userId) {
        blockTagRepository.deleteByTradeLogId(userId, tradeLogId);
        blockTickerRepository.deleteByTradeLogId(userId, tradeLogId);
    }

    /** Redis Tag Key 생성 로직 */
    private Set<String> generateLexEntries(Long userId, String name, Long targetId) {
        String suffix = "*" + name + "*" + targetId;
        return generateCommonRedisKeys(userId, name, suffix);
    }

    /** Redis Ticker Key 생성 로직 */
    private Set<String> generateSearchKeywords(Long userId, String name, Long tickerId, String symbol) {
        String suffix = "*" + name + "*" + symbol + "*" + tickerId;
        return generateCommonRedisKeys(userId, name, suffix);
    }

    /** Redis 검색 키 생성을 위한 공통 템플릿 로직 */
    private Set<String> generateCommonRedisKeys(Long userId, String name, String suffix) {
        String prefix = userId + ":";

        return Stream.of(
                        hangulConverter.jasoConvert(name),
                        hangulConverter.chosungConvert(name)
                )
                .map(converted -> prefix + converted + suffix)
                .collect(Collectors.toSet());
    }

}
