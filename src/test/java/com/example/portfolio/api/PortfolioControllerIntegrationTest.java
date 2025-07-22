package com.example.portfolio.api;

import com.example.portfolio.domain.Portfolio;
import com.example.portfolio.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class PortfolioControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PortfolioRepository repository;

    @BeforeEach
    void setup() {
        // clear repository before each test
        repository.deleteById("1").block();
        repository.deleteById("2").block();
        repository.deleteById("3").block();
    }

    @Test
    void createPortfolioReturnsId() {
        webTestClient.post().uri("/portfolios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("userId", "u1", "name", "My Portfolio"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.portfolioId").value(id -> {
                    String portfolioId = id.toString();
                    assertThat(repository.findById(portfolioId).block()).isNotNull();
                });
    }

    @Test
    void getPortfolioByIdReturnsPortfolio() {
        Portfolio portfolio = new Portfolio("1", "u1", List.of(), "Test", Instant.now());
        repository.save(portfolio).block();

        webTestClient.get().uri("/portfolios/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.portfolioId").isEqualTo("1")
                .jsonPath("$.name").isEqualTo("Test");
    }

    @Test
    void updatePortfolioChangesName() {
        Portfolio portfolio = new Portfolio("2", "u1", List.of(), "Old", Instant.now());
        repository.save(portfolio).block();

        webTestClient.put().uri("/portfolios/2")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("name", "Updated"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Updated");

        assertThat(repository.findById("2").block().name()).isEqualTo("Updated");
    }

    @Test
    void getPortfoliosByUserReturnsList() {
        Portfolio p1 = new Portfolio("1", "uX", List.of(), "P1", Instant.now());
        Portfolio p2 = new Portfolio("2", "uX", List.of(), "P2", Instant.now());
        repository.save(p1).block();
        repository.save(p2).block();

        webTestClient.get().uri("/portfolios/user/uX")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class).hasSize(2);
    }

    @Test
    void deletePortfolioRemovesPortfolio() {
        Portfolio portfolio = new Portfolio("3", "u1", List.of(), "ToDelete", Instant.now());
        repository.save(portfolio).block();

        webTestClient.delete().uri("/portfolios/3")
                .exchange()
                .expectStatus().isNoContent();

        assertThat(repository.findById("3").block()).isNull();
    }
}
