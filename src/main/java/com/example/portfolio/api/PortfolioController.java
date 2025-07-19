package com.example.portfolio.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class PortfolioController {
    @GetMapping("/portfolio")
    public Mono<String> getPortfolio() {
        return Mono.just("OK");
    }
}
