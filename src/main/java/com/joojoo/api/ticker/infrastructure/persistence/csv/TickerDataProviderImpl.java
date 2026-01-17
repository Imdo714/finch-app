package com.joojoo.api.ticker.infrastructure.persistence.csv;

import com.joojoo.api.ticker.application.port.out.TickerMappingStrategy;
import com.joojoo.api.ticker.application.port.out.TickerDataProvider;
import com.joojoo.api.ticker.infrastructure.persistence.csv.strategy.DefaultTickerStrategy;
import com.joojoo.api.ticker.infrastructure.persistence.csv.strategy.NasdaqTickerStrategy;
import com.joojoo.api.ticker.presentation.dto.request.TickerDataDto;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class TickerDataProviderImpl implements TickerDataProvider {

    private static final String FILE_PATH = "data/stocks.csv";
    private static final String NASDAQ_FILE_PATH = "data/nasdaq.csv";

    @Override
    public List<TickerDataDto> getTickerCsvData() {
        return readCsv(FILE_PATH, new DefaultTickerStrategy());
    }

    @Override
    public List<TickerDataDto> fetchNasdaqTickers() {
        return readCsv(NASDAQ_FILE_PATH, new NasdaqTickerStrategy());
    }

    private List<TickerDataDto> readCsv(String path, TickerMappingStrategy strategy) {
        ClassPathResource resource = new ClassPathResource(path);

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            List<TickerDataDto> result = new ArrayList<>();
            List<String[]> rows = csvReader.readAll();

            for (int i = 1; i < rows.size(); i++) {
                // 전달받은 전략에 따라 구현체 변환
                strategy.TickerConverter(rows.get(i)).ifPresent(result::add);
            }
            return result;
        } catch (Exception e) {
            log.error("CSV 처리 실패: {}", path, e);
            throw new RuntimeException(e);
        }
    }

}
