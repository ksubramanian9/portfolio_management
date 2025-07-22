package com.example.portfolio.event;

import com.example.portfolio.domain.Currency;
import java.time.Instant;

public record TradeExecuted(
        String transactionId,
        String portfolioId,
        String assetId,
        double quantity,
        String transactionType,
        double price,
        Currency currency,
        Instant timestamp
) {}
