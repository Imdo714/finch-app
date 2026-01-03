package com.joojoo.api.metadata.infrastructure.persistence;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import com.joojoo.api.metadata.application.port.out.MetadataPort;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.global.util.HangulUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MetadataPersistenceAdapter implements MetadataPort { // User 도메인에 있는 어뎁터도 수정해야 함

    private final BlockTickerRepository blockTickerRepository;
    private final BlockTagRepository blockTagRepository;

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
            Long tagId = tag.getId();
            lexEntries.add(userId + ":" + HangulUtils.splitToJaso(name) + "*" + name + "*" + tagId);
            lexEntries.add(userId + ":" + HangulUtils.getChosung(name) + "*" + name + "*" + tagId);
            tagIds.add(tagId);
        });

        blockTagRepository.addTagsToRedis(userId, lexEntries, tagIds);
    }

}
