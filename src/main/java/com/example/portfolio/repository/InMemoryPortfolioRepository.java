package com.example.portfolio.repository;

import com.example.portfolio.domain.Portfolio;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPortfolioRepository implements PortfolioRepository {

    private final Map<String, Portfolio> store = new ConcurrentHashMap<>();

    @Override
    public Mono<Portfolio> findById(String id) {
        return Mono.justOrEmpty(store.get(id));
    }

    @Override
    public Flux<Portfolio> findByUserId(String userId) {
        return Flux.fromIterable(store.values())
                .filter(p -> p.userId().equals(userId));
    }

    @Override
    public Mono<Portfolio> save(Portfolio portfolio) {
        store.put(portfolio.portfolioId(), portfolio);
        return Mono.just(portfolio);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        store.remove(id);
        return Mono.empty();
    }
}
