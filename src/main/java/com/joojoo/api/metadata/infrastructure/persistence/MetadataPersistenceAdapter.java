package com.joojoo.api.metadata.infrastructure.persistence;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTag.infrastructure.redis.BlockTagRedisRepository;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import com.joojoo.api.common.hangul.HangulConverter;
import com.joojoo.api.metadata.application.port.out.MetadataPort;
import com.joojoo.api.tag.domain.model.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MetadataPersistenceAdapter implements MetadataPort { // User 도메인에 있는 어뎁터도 수정해야 함

    private final BlockTickerRepository blockTickerRepository;
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
    public void processRedisTagRemoval(Long userId, List<BlockTag> oldTags) {
        Map<Tag, Long> tagCounts = oldTags.stream()
                .collect(Collectors.groupingBy(BlockTag::getTag, Collectors.counting()));

        tagCounts.forEach((tag, count) -> {
            Set<String> lexEntries = generateLexEntries(userId, tag.getName(), tag.getId());
            blockTagRedisRepository.removeTagsFromRedis(userId, tag.getId(), lexEntries, count.intValue());
        });
    }

    /** Redis Tag Key 생성 로직 */
    private Set<String> generateLexEntries(Long userId, String name, Long tagId) {
        String base = "*" + name + "*" + tagId;

        return Set.of(
                userId + ":" + hangulConverter.jasoConvert(name) + base,
                userId + ":" + hangulConverter.chosungConvert(name) + base
        );
    }

    @Override
    public List<BlockTag> findAllTagsByBlockId(Long blockId) {
        return blockTagRepository.findAllTagsByBlockId(blockId);
    }

    @Override
    public void deleteMetadataByBlockId(Long blockId) {
        blockTickerRepository.deleteByBlockIds(blockId);
        blockTagRepository.deleteByBlockIds(blockId);
    }

}
