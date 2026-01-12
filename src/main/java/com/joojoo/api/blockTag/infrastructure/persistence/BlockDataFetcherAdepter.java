package com.joojoo.api.blockTag.infrastructure.persistence;

import com.joojoo.api.block.application.port.out.LoadDailyDetailsPort;
import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.blockTag.application.port.out.BlockDataFetcher;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.presentation.dto.request.BlockRelatedDataBundle;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockDataFetcherAdepter implements BlockDataFetcher {

    private final BlockRepository blockRepository;
    private final LoadDailyDetailsPort loadDailyDetailsPort;

    @Override
    public BlockRelatedDataBundle fetchRelatedData(List<Long> blockIds, List<Long> tradeLogIds) {
        List<BlockTag> tags = loadDailyDetailsPort.findAllBlockTags(blockIds, tradeLogIds);
        List<BlockTicker> tickers = loadDailyDetailsPort.findAllBlockTickers(blockIds, tradeLogIds);
        Map<Long, Long> childCounts = blockRepository.getChildCounts(blockIds);

        return new BlockRelatedDataBundle(
                getTagsByBlockId(tags),
                getTickersByBlockId(tickers),
                childCounts,
                getTagsByTradeLogId(tags),
                getTickersByTradeLogId(tickers)
        );
    }

    private static Map<Long, List<BlockTicker>> getTickersByTradeLogId(List<BlockTicker> tickers) {
        return tickers.stream()
                .filter(t -> t.getTradeLog() != null)
                .collect(Collectors.groupingBy(t -> t.getTradeLog().getId()));
    }

    private static Map<Long, List<BlockTicker>> getTickersByBlockId(List<BlockTicker> tickers) {
        return tickers.stream()
                .filter(t -> t.getBlock() != null)
                .collect(Collectors.groupingBy(t -> t.getBlock().getId()));
    }

    private static Map<Long, List<BlockTag>> getTagsByTradeLogId(List<BlockTag> tags) {
        return tags.stream()
                .filter(t -> t.getTradeLog() != null)
                .collect(Collectors.groupingBy(t -> t.getTradeLog().getId()));
    }

    private static Map<Long, List<BlockTag>> getTagsByBlockId(List<BlockTag> tags) {
        return tags.stream()
                .filter(t -> t.getBlock() != null)
                .collect(Collectors.groupingBy(t -> t.getBlock().getId()));
    }
}
