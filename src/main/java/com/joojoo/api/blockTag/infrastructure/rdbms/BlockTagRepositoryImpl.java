package com.joojoo.api.blockTag.infrastructure.rdbms;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTag.infrastructure.queryDsl.BlockTagQueryDslRepository;
import com.joojoo.api.blockTag.infrastructure.queryDsl.date.BlockTagDateQueryDslRepository;
import com.joojoo.api.blockTag.presentation.dto.response.all.TagDateResult;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagCountResponse;
import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BlockTagRepositoryImpl implements BlockTagRepository {

    private final BlockTagJpaRepository blockTagJpaRepository;
    private final BlockTagQueryDslRepository blockTagQueryDslRepository;
    private final BlockTagDateQueryDslRepository blockTagDateQueryDslRepository;

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
    public BlockTagCountResponse getBlockCount(Long userId, Long tagId) {
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
}
