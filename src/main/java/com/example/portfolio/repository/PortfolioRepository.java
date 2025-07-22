package com.example.portfolio.repository;

import com.example.portfolio.domain.Portfolio;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PortfolioRepository {
    Mono<Portfolio> findById(String id);
    Flux<Portfolio> findByUserId(String userId);
    Mono<Portfolio> save(Portfolio portfolio);
    Mono<Void> deleteById(String id);
}
