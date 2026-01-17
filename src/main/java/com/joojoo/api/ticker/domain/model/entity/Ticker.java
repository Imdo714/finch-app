package com.joojoo.api.ticker.domain.model.entity;

import com.joojoo.api.ticker.domain.model.enums.MarketType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tickers")
public class Ticker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", nullable = false, unique = true)
    private String symbol;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private MarketType market;

    @Column(name = "listing_date")
    private LocalDate listingDate; // 상장 일

    @Column(name = "delisting_date")
    private LocalDate delistingDate; // 폐지 일

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Builder
    public Ticker(String symbol, String name, MarketType market, LocalDate listingDate) {
        this.symbol = symbol;
        this.name = name;
        this.market = market;
        this.listingDate = listingDate;
    }

    public static Ticker create(String symbol, String name, MarketType market, LocalDate listingDate) {
        return Ticker.builder()
                .symbol(symbol)
                .name(name)
                .market(market)
                .listingDate(listingDate)
                .build();
    }

    public void updateInfo(String name, MarketType market, LocalDate listingDate) {
        if (!this.name.equals(name)) this.name = name;
        this.market = market;
        this.listingDate = listingDate;
    }

}
