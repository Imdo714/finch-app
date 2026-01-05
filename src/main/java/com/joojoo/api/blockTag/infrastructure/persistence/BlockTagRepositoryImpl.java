package com.joojoo.api.blockTag.infrastructure.persistence;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTag.infrastructure.queryDsl.BlockTagQueryDslRepository;
import com.joojoo.api.blockTag.infrastructure.queryDsl.date.BlockTagDateQueryDslRepository;
import com.joojoo.api.blockTag.infrastructure.rdbms.BlockTagJpaRepository;
import com.joojoo.api.blockTag.infrastructure.redis.BlockTagRedisRepository;
import com.joojoo.api.blockTag.presentation.dto.response.all.TagDateResult;
import com.joojoo.global.common.response.detail.count.RelatedBlockDetailCountResponse;
import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class BlockTagRepositoryImpl implements BlockTagRepository {

    private final BlockTagJpaRepository blockTagJpaRepository;
    private final BlockTagQueryDslRepository blockTagQueryDslRepository;
    private final BlockTagDateQueryDslRepository blockTagDateQueryDslRepository;
    private final BlockTagRedisRepository blockTagRedisRepository;

    @Override
    public List<BlockTag> saveAll(List<BlockTag> blockTags) {
        return blockTagJpaRepository.saveAll(blockTags);
    }

    @Override
    public List<BlockTag> findAllBlockTags(List<Long> blockIds) {
        return blockTagQueryDslRepository.findAllBlockTags(blockIds);
    }

    @Override
    public List<RecentTagsResponse.RecentTagsDto> findRecentTags(Long userId) {
        return blockTagQueryDslRepository.findRecentTags(userId);
    }

    @Override
    public RelatedBlockDetailCountResponse getBlockCount(Long userId, Long tagId) {
        return blockTagQueryDslRepository.getBlockCount(userId, tagId);
    }

    @Override
    public void deleteByBlockIds(Long id) {
        blockTagJpaRepository.deleteByBlockIds(id);
    }

    @Override
    public TagDateResult findAllByTagAndDate(Long userId, Long tagId, LocalDate targetDate) {
        return blockTagDateQueryDslRepository.findAllByTagAndDate(userId, tagId, targetDate);
    }

    @Override
    public List<BlockTag> findAllTagsByTradeLogIds(List<Long> tradeLogIds) {
        return blockTagQueryDslRepository.findAllTagsByTradeLogIds(tradeLogIds);
    }

    @Override
    public void addTagsToRedis(Long userId, Set<String> lexEntries, Set<Long> tagIds) {
        blockTagRedisRepository.addTagsToRedis(userId, lexEntries, tagIds);
    }

    @Override
    public void removeTagsFromRedis(Long userId, Long tagId, Set<String> lexEntries, int countToRemove) {
        blockTagRedisRepository.removeTagsFromRedis(userId, tagId, lexEntries, countToRemove);
    }

    @Override
    public List<BlockTag> findAllByBlockIdIn(List<Long> blockIds) {
        return blockTagQueryDslRepository.findAllByBlockIdIn(blockIds);
    }

    @Override
    public List<BlockTag> findAllTagsByBlockId(Long blockId) {
        return blockTagQueryDslRepository.findAllTagsByBlockId(blockId);
    }

    @Override
    public Set<String> searchTagQuery(String prefix) {
        return blockTagRedisRepository.searchTagQuery(prefix);
    }

}
