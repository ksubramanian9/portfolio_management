package com.example.portfolio.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PortfolioTest {
    @Test
    void portfolioCanBeInstantiated() {
        Portfolio portfolio = new Portfolio("1", "Test");
        assertThat(portfolio.id()).isEqualTo("1");
    }
}
