package com.joojoo.api.blockTag.presentation.dto.response.all;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TradeLogResponseDto {
    private Long tradeLogId;
    private Long tickerId;
    private String tickerName;   // 종목명
    private String tickerSymbol;
    private String tradeType;    // BUY(매수), SELL(매도)
    private BigDecimal price;    // 매매가
    private BigDecimal amount;   // 수량
    private String currency;
    private LocalDateTime executedAt; // 매매 일시
    private LocalDateTime createdAt;
    private String memo;         // 매매 메모
    private String riskFactor;
    private String tradingPlan;
    private List<BlockDetailMode.MetadataResponse> tagNames; // 매매일지에 달린 태그
    private List<BlockDetailMode.MetadataResponse> tickerNames;

    /**
     * TradeLog 엔티티와 해당 로그에 달린 태그 리스트를 DTO로 변환
     */
    public static TradeLogResponseDto from(TradeLog log, List<BlockTag> tagsForThisLog, List<BlockTicker> tickersForThisLog) {
        return TradeLogResponseDto.builder()
                .tradeLogId(log.getId())
                .tickerId(log.getTicker().getId())
                .tickerName(log.getTicker().getName())
                .tickerSymbol(log.getTicker().getSymbol())
                .tradeType(log.getTradeType().name())
                .price(log.getPrice())
                .amount(log.getAmount())
                .currency(log.getCurrency())
                .executedAt(log.getExecutedAt())
                .createdAt(log.getCreatedAt())
                .memo(log.getMemo())
                .riskFactor(log.getRiskFactor())
                .tradingPlan(log.getTradingPlan())
                .tagNames(tagsForThisLog.stream()
                        .map(bt -> BlockDetailMode.MetadataResponse.of(
                                bt.getTag().getId(),
                                bt.getTag().getName(),
                                bt.getFieldType(),
                                bt.getSequence(),
                                bt.getStartOffset()))
                        .toList())
                .tickerNames(tickersForThisLog.stream()
                        .map(bt -> BlockDetailMode.MetadataResponse.of(
                                bt.getTicker().getId(),
                                bt.getTicker().getName(),
                                bt.getFieldType(),
                                bt.getSequence(),
                                bt.getStartOffset()))
                        .toList())
                .build();
    }
}
