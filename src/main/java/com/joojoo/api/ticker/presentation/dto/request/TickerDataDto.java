package com.joojoo.api.ticker.presentation.dto.request;

import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.ticker.domain.model.enums.MarketType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class TickerDataDto {
    private String symbol;
    private String name;
    private MarketType market;
    private LocalDate listingDate;

    public static List<Ticker> getTickerList(List<TickerDataDto> externalStocks, Map<String, Ticker> tickerMap) {
        return externalStocks.stream()
                .map(data -> {
                    Ticker ticker = tickerMap.get(data.getSymbol());
                    if (ticker != null) {
                        ticker.updateInfo(data.getName(), data.getMarket(), data.getListingDate());
                        return ticker;
                    }
                    return Ticker.create(data.getSymbol(), data.getName(), data.getMarket(), data.getListingDate());
                })
                .collect(Collectors.toList());
    }

    /** 국내 주식 CSV 매핑 */
    public static Optional<TickerDataDto> fromDefaultCsv(String[] row) {
        if (row.length < 7 || row[1] == null || row[1].isBlank()) {
            return Optional.empty();
        }

        try {
            return Optional.of(new TickerDataDto(
                    row[1],
                    row[3],
                    MarketType.fromString(row[6]),
                    LocalDate.parse(row[5], DateTimeFormatter.ISO_DATE)
            ));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /** 나스닥 CSV 매핑 */
    public static Optional<TickerDataDto> fromNasdaqCsv(String[] row) {
        if (row.length < 2 || row[0] == null || row[0].isBlank() || row[1] == null || row[1].isBlank()) {
            return Optional.empty();
        }

        try {
            String symbol = row[0].trim();
            String name = row[1].trim();

            if (symbol.isBlank() || name.isBlank()) {
                return Optional.empty();
            }

            return Optional.of(new TickerDataDto(
                    symbol,
                    name,
                    MarketType.NASDAQ,
                    null
            ));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
