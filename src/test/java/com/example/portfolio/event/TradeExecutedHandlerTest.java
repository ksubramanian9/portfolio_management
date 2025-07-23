package com.example.portfolio.event;

import com.example.portfolio.domain.Asset;
import com.example.portfolio.domain.Currency;
import com.example.portfolio.domain.Portfolio;
import com.example.portfolio.repository.InMemoryPortfolioRepository;
import com.example.portfolio.repository.PortfolioRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TradeExecutedHandlerTest {

    @Test
    void handleUpdatesPortfolioAndPublishesEvent() {
        PortfolioRepository repo = new InMemoryPortfolioRepository();
        InMemoryEventPublisher publisher = new InMemoryEventPublisher();
        TradeExecutedHandler handler = new TradeExecutedHandler(repo, publisher);

        Portfolio portfolio = new Portfolio("p1", "u1", List.of(), "Test", Instant.now());
        repo.save(portfolio).block();

        TradeExecuted trade = new TradeExecuted(
                "t1",
                "p1",
                "AAPL",
                10.0,
                "BUY",
                150.0,
                new Currency("USD"),
                Instant.now()
        );

        handler.handle(trade).block();

        Portfolio updated = repo.findById("p1").block();
        assertThat(updated).isNotNull();
        assertThat(updated.assets()).hasSize(1);
        Asset asset = updated.assets().get(0);
        assertThat(asset.assetId()).isEqualTo("AAPL");
        assertThat(asset.quantity()).isEqualTo(10.0);

        assertThat(publisher.events()).hasSize(1);
        Object evt = publisher.events().get(0);
        assertThat(evt).isInstanceOf(PortfolioUpdated.class);
        PortfolioUpdated pu = (PortfolioUpdated) evt;
        assertThat(pu.portfolioId()).isEqualTo("p1");
    }
}
