package com.example.portfolio.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PortfolioTest {
    @Test
    void portfolioCanBeInstantiated() {
        Portfolio portfolio = new Portfolio("1", "u1", List.of(), "Test", Instant.now());
        assertThat(portfolio.portfolioId()).isEqualTo("1");
        assertThat(portfolio.assets()).isEmpty();
    }
}
