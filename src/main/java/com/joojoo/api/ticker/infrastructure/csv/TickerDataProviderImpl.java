package com.joojoo.api.ticker.infrastructure.csv;

import com.joojoo.api.ticker.domain.model.enums.MarketType;
import com.joojoo.api.ticker.domain.provider.TickerDataProvider;
import com.joojoo.api.ticker.presentation.dto.request.TickerDataDto;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class TickerDataProviderImpl implements TickerDataProvider {

    private static final String FILE_PATH = "data/stocks.csv";

    @Override
    public List<TickerDataDto> getTickerCsvData() {
        List<TickerDataDto> result = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource(FILE_PATH);

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> rows = csvReader.readAll();

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (isValidRow(row)) {
                    result.add(mapToDto(row));
                }
            }
        } catch (Exception e) {
            log.error("CSV 파싱 실패", e);
            throw new RuntimeException("주식 데이터 로드 실패", e);
        }
        return result;
    }

    private boolean isValidRow(String[] row) {
        return row.length >= 7 && row[1] != null && !row[1].isBlank();
    }

    private TickerDataDto mapToDto(String[] row) {
        return new TickerDataDto(
                row[1], // symbol
                row[3], // name
                MarketType.fromString(row[6]),
                LocalDate.parse(row[5], DateTimeFormatter.ISO_DATE)
        );
    }
}
