package com.example.portfolio.event;

import com.example.portfolio.domain.Asset;
import com.example.portfolio.domain.Currency;
import com.example.portfolio.domain.Portfolio;
import com.example.portfolio.domain.PortfolioValue;
import com.example.portfolio.repository.PortfolioRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TradeExecutedHandler {

    private final PortfolioRepository repository;
    private final EventPublisher publisher;

    public TradeExecutedHandler(PortfolioRepository repository, EventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public Mono<Void> handle(TradeExecuted event) {
        return repository.findById(event.portfolioId())
                .flatMap(portfolio -> {
                    List<Asset> updatedAssets = updateAssets(portfolio.assets(), event);
                    Portfolio updated = new Portfolio(
                            portfolio.portfolioId(),
                            portfolio.userId(),
                            updatedAssets,
                            portfolio.name(),
                            portfolio.createdAt()
                    );
                    return repository.save(updated)
                            .flatMap(saved -> publishUpdated(saved, event.currency()));
                })
                .then();
    }

    private List<Asset> updateAssets(List<Asset> assets, TradeExecuted event) {
        List<Asset> result = new ArrayList<>(assets);
        Optional<Asset> existing = result.stream()
                .filter(a -> a.assetId().equals(event.assetId()))
                .findFirst();
        double qtyChange = event.quantity() * ("BUY".equalsIgnoreCase(event.transactionType()) ? 1 : -1);
        if (existing.isPresent()) {
            result.remove(existing.get());
            double newQty = existing.get().quantity() + qtyChange;
            if (newQty > 0) {
                result.add(new Asset(event.assetId(), newQty, existing.get().currency()));
            }
        } else if (qtyChange > 0) {
            result.add(new Asset(event.assetId(), qtyChange, event.currency()));
        }
        return result;
    }

    private Mono<Void> publishUpdated(Portfolio portfolio, Currency currency) {
        double totalQty = portfolio.assets().stream().mapToDouble(Asset::quantity).sum();
        PortfolioValue value = new PortfolioValue(totalQty, currency);
        PortfolioUpdated updatedEvent = new PortfolioUpdated(
                portfolio.portfolioId(),
                value,
                Instant.now()
        );
        return publisher.publish(updatedEvent);
    }
}
