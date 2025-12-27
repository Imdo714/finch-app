package com.joojoo.api.tradeLogTag.domain.repository;

import com.joojoo.api.tradeLogTag.domain.entity.TradeLogTag;

import java.util.List;

public interface TradeLogTagRepository {
    void saveAll(List<TradeLogTag> tradeLogTags);
}
