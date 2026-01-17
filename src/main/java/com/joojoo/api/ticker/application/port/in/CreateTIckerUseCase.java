package com.joojoo.api.ticker.application.port.in;

public interface CreateTIckerUseCase {

    // 배포하지는 않고 로컬에서 csv 파일 실행 해 운영 DB에 데이터 넣기
    void initKoreaTickerData();

    void initNasdaqTickerData();

    void initAmexTickerData();
}
