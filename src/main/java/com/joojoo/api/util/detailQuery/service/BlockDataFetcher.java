package com.joojoo.api.util.detailQuery.service;

import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.blockTag.application.test.QueryDsl;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import com.joojoo.api.util.detailQuery.dto.BlockRelatedDataBundle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockDataFetcher {
    private final BlockTagRepository blockTagRepository;
    private final BlockTickerRepository blockTickerRepository;
    private final BlockRepository blockRepository;
    private final QueryDsl queryDsl;

    public BlockRelatedDataBundle fetchRelatedData(List<Long> blockIds, List<Long> tradeLogIds) {
        return new BlockRelatedDataBundle(
                blockTagRepository.findAllBlockTags(blockIds).stream()
                        .collect(Collectors.groupingBy(bt -> bt.getBlock().getId())),

                blockTickerRepository.findAllBlockTickers(blockIds).stream()
                        .collect(Collectors.groupingBy(bt -> bt.getBlock().getId())),

                blockRepository.getChildCounts(blockIds),

                queryDsl.findAllByTradeLogIds(tradeLogIds).stream()
                        .collect(Collectors.groupingBy(bt -> bt.getTradeLog().getId())),

                queryDsl.findAllTickersByTradeLogIds(tradeLogIds).stream()
                        .collect(Collectors.groupingBy(bt -> bt.getTradeLog().getId()))
        );
    }
}
