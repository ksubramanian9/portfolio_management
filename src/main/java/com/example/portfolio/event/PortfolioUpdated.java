package com.example.portfolio.event;

import com.example.portfolio.domain.PortfolioValue;
import java.time.Instant;

public record PortfolioUpdated(
        String portfolioId,
        PortfolioValue newValue,
        Instant timestamp
) {}
