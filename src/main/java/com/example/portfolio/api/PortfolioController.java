package com.example.portfolio.api;

import com.example.portfolio.api.dto.CreatePortfolioRequest;
import com.example.portfolio.api.dto.UpdatePortfolioRequest;
import com.example.portfolio.domain.Portfolio;
import com.example.portfolio.repository.PortfolioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/portfolios")
public class PortfolioController {

    private final PortfolioRepository repository;

    public PortfolioController(PortfolioRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Map<String, String>> create(@RequestBody Mono<CreatePortfolioRequest> request) {
        return request.flatMap(req -> {
            String id = UUID.randomUUID().toString();
            Portfolio portfolio = new Portfolio(id, req.userId(), List.of(), req.name(), Instant.now());
            return repository.save(portfolio)
                    .map(p -> Map.of("portfolioId", p.portfolioId()));
        });
    }

    @GetMapping("/{id}")
    public Mono<Portfolio> getById(@PathVariable String id) {
        return repository.findById(id);
    }

    @GetMapping("/user/{userId}")
    public Flux<Portfolio> getByUser(@PathVariable String userId) {
        return repository.findByUserId(userId);
    }

    @PutMapping("/{id}")
    public Mono<Portfolio> update(@PathVariable String id, @RequestBody Mono<UpdatePortfolioRequest> request) {
        return repository.findById(id)
                .flatMap(existing -> request.flatMap(req -> {
                    Portfolio updated = new Portfolio(
                            id,
                            existing.userId(),
                            existing.assets(),
                            req.name(),
                            existing.createdAt()
                    );
                    return repository.save(updated);
                }));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return repository.deleteById(id);
    }
}
