package com.joojoo.api.blockTag.application.port.out;

import com.joojoo.api.blockTag.presentation.dto.request.BlockRelatedDataBundle;

import java.util.List;

public interface BlockDataFetcher {
    BlockRelatedDataBundle fetchRelatedData(List<Long> blockIds, List<Long> tradeLogIds);
}
