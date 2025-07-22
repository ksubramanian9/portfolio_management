package com.example.portfolio.domain;

import java.time.Instant;
import java.util.List;

public record Portfolio(String portfolioId,
                        String userId,
                        List<Asset> assets,
                        String name,
                        Instant createdAt) {}
