package com.joojoo.api.ticker.domain.service;

import com.joojoo.api.common.hangul.HangulConverter;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class SearchKeywordMapper {

    private final HangulConverter hangulConverter;
    private static final String DELIMITER = "*";

    public Stream<String> mapToSearchIndex(Ticker stock) {
        String name = stock.getName();
        String symbol = stock.getSymbol();
        Long id = stock.getId();

        if (stock.getMarket().isKorea()) {
            return createKoreaSearchIndex(name, symbol, id);
        }
        return createAmericaSearchIndex(name, symbol, id);
    }

    /** 한국 주식: 자소 및 초성 기반 인덱스 생성 */
    private Stream<String> createKoreaSearchIndex(String name, String symbol, Long id) {
        return Stream.of(
                hangulConverter.jasoConvert(name) + DELIMITER + name + DELIMITER + symbol + DELIMITER + id,
                hangulConverter.chosungConvert(name) + DELIMITER + name + DELIMITER + symbol + DELIMITER + id
        );
    }

    /** 미국 주식: 대문자 변환 기반(명칭/심볼) 인덱스 생성 */
    private Stream<String> createAmericaSearchIndex(String name, String symbol, Long id) {
        String upperName = name.toUpperCase();
        String upperSymbol = symbol.toUpperCase();

        return Stream.of(
                upperName + DELIMITER + name + DELIMITER + symbol + DELIMITER + id,
                upperSymbol + DELIMITER + name + DELIMITER + symbol + DELIMITER + id
        );
    }
}
