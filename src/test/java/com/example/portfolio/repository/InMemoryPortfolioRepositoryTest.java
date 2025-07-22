package com.example.portfolio.repository;

import com.example.portfolio.domain.Portfolio;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryPortfolioRepositoryTest {

    @Test
    void saveAndRetrievePortfolio() {
        PortfolioRepository repo = new InMemoryPortfolioRepository();
        Portfolio portfolio = new Portfolio("1", "u1", List.of(), "Test", Instant.now());
        repo.save(portfolio).block();

        Portfolio found = repo.findById("1").block();
        assertThat(found).isEqualTo(portfolio);
    }
}
