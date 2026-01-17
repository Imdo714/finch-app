package com.joojoo.api.ticker.application.port.out;

import com.joojoo.api.ticker.presentation.dto.request.TickerDataDto;

import java.util.List;

public interface TickerDataProvider {
    /** 국내 주식 Ticker 읽어오기 */
    List<TickerDataDto> getTickerCsvData();
    
    /** 나스닥 Ticker 읽어오기 */
    List<TickerDataDto> fetchNasdaqTickers();

    /** 아멕스 Ticker 읽어오기 */
    List<TickerDataDto> fetchAmexTickers();
}
