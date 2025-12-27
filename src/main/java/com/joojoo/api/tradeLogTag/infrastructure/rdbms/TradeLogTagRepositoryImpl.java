package com.joojoo.api.tradeLogTag.infrastructure.rdbms;

import com.joojoo.api.tradeLogTag.domain.entity.TradeLogTag;
import com.joojoo.api.tradeLogTag.domain.repository.TradeLogTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TradeLogTagRepositoryImpl implements TradeLogTagRepository {

    private final TradeLogTagJpaRepository tradeLogTagJpaRepository;

    @Override
    public void saveAll(List<TradeLogTag> tradeLogTags) {
        tradeLogTagJpaRepository.saveAll(tradeLogTags);
    }
}
