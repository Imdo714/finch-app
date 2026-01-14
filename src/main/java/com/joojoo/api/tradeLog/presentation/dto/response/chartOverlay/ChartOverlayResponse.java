package com.joojoo.api.tradeLog.presentation.dto.response.chartOverlay;

import com.joojoo.api.common.domain.enums.TradeType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChartOverlayResponse {

    private List<TradeDetailDto> buyLogs;  // 매수 기록 리스트
    private List<TradeDetailDto> sellLogs; // 매도 기록 리스트
    private List<AnalysisBlockDto> analysisBlocks; // 블록(분석) 리스트

    @Builder
    public ChartOverlayResponse(List<TradeDetailDto> buyLogs,
                                List<TradeDetailDto> sellLogs,
                                List<AnalysisBlockDto> analysisBlocks) {
        this.buyLogs = buyLogs;
        this.sellLogs = sellLogs;
        this.analysisBlocks = analysisBlocks;
    }

    public static ChartOverlayResponse of(List<TradeDetailDto> logs, List<AnalysisBlockDto> blocks) {
        return ChartOverlayResponse.builder()
                .buyLogs(filterByTradeType(logs, TradeType.BUY))
                .sellLogs(filterByTradeType(logs, TradeType.SELL))
                .analysisBlocks(blocks)
                .build();
    }

    private static List<TradeDetailDto> filterByTradeType(List<TradeDetailDto> logs, TradeType type) {
        return logs.stream()
                .filter(log -> log.getTradeType() == type)
                .toList();
    }

    @Getter
    @NoArgsConstructor
    public static class TradeDetailDto {
        private Long id;
        private LocalDateTime executedAt; // 매매 시간
        private TradeType tradeType;

        @Builder
        public TradeDetailDto(Long id, LocalDateTime executedAt, TradeType tradeType) {
            this.id = id;
            this.executedAt = executedAt;
            this.tradeType = tradeType;
        }

        public static TradeDetailDto from(TradeDetailDto entity) {
            return TradeDetailDto.builder()
                    .id(entity.getId())
                    .executedAt(entity.getExecutedAt())
                    .tradeType(entity.getTradeType())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnalysisBlockDto {
        private Long id;
        private String content;
        private LocalDateTime createdAt;
    }

}
