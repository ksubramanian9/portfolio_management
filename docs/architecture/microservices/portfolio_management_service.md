# Portfolio Management Service

The Portfolio Management Service is a core microservice in the Investment Portfolio Manager application, responsible for managing portfolios, including creation, updates, and asset allocation. Built using **Java** with **Spring Boot**, it aligns with microservices, Domain-Driven Design (DDD), and Event-Driven Architecture (EDA) paradigms. This document outlines the service’s responsibilities, domain model, event handling, API endpoints, integrations, and technical considerations, ensuring compliance with non-functional requirements like scalability, security, reliability, and performance.

## Overview
- **Bounded Context**: Portfolio Management
- **Responsibilities**:
  - Create and manage portfolios for users (individual investors, advisors, institutional clients).
  - Track portfolio composition, including assets and their allocations.
  - Update portfolios in response to events (e.g., trades, dividends).
  - Provide portfolio-level metrics (e.g., total value, allocation percentages).
  - Ensure compliance with investment constraints (e.g., diversification rules).
- **Paradigms**:
  - **DDD**: Defines a clear bounded context with a ubiquitous language (e.g., "portfolio," "asset").
  - **EDA**: Responds to events like "TradeExecuted" or "PriceUpdated" for real-time updates.
  - **FP**: Uses immutable data structures and pure functions for reliable computations.
  - **Microservices**: Operates as an independent service, communicating via APIs and events.

## Domain Model
The domain model follows DDD principles, using entities, aggregates, value objects, repositories, and domain services. It is implemented in Java, leveraging immutable data structures and clean code practices.

- **Entity**: Portfolio
  - **Attributes**:
    - `portfolioId: String` (unique identifier)
    - `userId: String` (owner of the portfolio)
    - `assets: List[Asset]` (list of assets in the portfolio)
    - `name: String` (portfolio name, e.g., "Retirement Fund")
    - `createdAt: Date` (creation timestamp)
  - **Description**: Represents a user’s investment portfolio, encapsulating its state and behavior.

- **Aggregate**: Portfolio
  - **Root Entity**: Portfolio
  - **Description**: The Portfolio aggregate includes Assets and enforces consistency rules (e.g., valid asset allocations). All operations on assets within the portfolio go through the Portfolio aggregate root.

- **Value Objects**:
  - **Currency**: Represents a currency (e.g., USD, EUR) with immutable attributes.
    ```java
    public record Currency(code: String)
    ```
  - **Percentage**: Represents allocation weights (e.g., 30% equities).
    ```java
    public record Percentage(double value) {
        public Percentage {
            if (value < 0 || value > 100) {
                throw new IllegalArgumentException("Percentage must be between 0 and 100");
            }
        }
    }
  - **PortfolioValue**: Represents the total value of the portfolio in a given currency.
    ```java
    public record PortfolioValue(amount: Double, currency: Currency)
    ```

- **Repository**: PortfolioRepository
  - **Methods**:
    - `findById(portfolioId: String): Option[Portfolio]`
    - `save(portfolio: Portfolio): Unit`
    - `update(portfolio: Portfolio): Unit`
    - `findByUserId(userId: String): List[Portfolio]`
  - **Description**: Manages persistence of Portfolio aggregates, abstracting database operations (e.g., using Slick for PostgreSQL).

- **Domain Service**: PortfolioPerformanceCalculator
  - **Description**: Computes portfolio-level metrics (e.g., total value, allocation percentages) using pure functions.
  - **Example**:
    ```java
      public class PortfolioPerformanceCalculator {
        public static PortfolioValue calculateValue(Portfolio portfolio, Map<String, Double> assetPrices) {
            double total = portfolio.assets().stream()
                .mapToDouble(a -> assetPrices.getOrDefault(a.id(), 0.0) * a.quantity())
                .sum();
            return new PortfolioValue(total, portfolio.assets().get(0).currency());
        }
      }
    ```

## Event Handling
The service participates in EDA by subscribing to and publishing events via Apache Kafka. Key events include:

- **Subscribed Events**:
  - **TradeExecuted**: Triggered by the Transaction Management Service when a buy/sell order is completed.
    - **Action**: Update the portfolio’s asset list.
    - **Example**:
      ```java
        public record TradeExecuted(String assetId, double quantity, String transactionType) {}
        public Portfolio handleTradeExecuted(Portfolio portfolio, TradeExecuted event) {
            List<Asset> updatedAssets = updateAssets(portfolio.assets(), event);
            return new Portfolio(portfolio.id(), updatedAssets);
        }
        ```
  - **PriceUpdated**: Triggered by the Asset Management Service when asset prices change.
    - **Action**: Recalculate portfolio value.
  - **DividendPaid**: Triggered by the Transaction Management Service for dividend distributions.
    - **Action**: Update cash holdings or reinvest.

- **Published Events**:
  - **PortfolioUpdated**: Published when the portfolio’s state changes (e.g., after a trade).
    ```java
    public record PortfolioUpdated(portfolioId: String, newValue: PortfolioValue)
    ```

## API Endpoints
The service exposes RESTful APIs using **Spring WebFlux** for reactive, non-blocking operations. All endpoints are secured with OAuth2 (via Keycloak).

| **Endpoint** | **Method** | **Description** | **Request Body** | **Response** |
|--------------|------------|-----------------|------------------|--------------|
| `/portfolios` | POST | Create a new portfolio | `{ "userId": "string", "name": "string" }` | `{ "portfolioId": "string" }` |
| `/portfolios/{id}` | GET | Retrieve portfolio details | - | `{ "portfolioId": "string", "assets": [], "value": {} }` |
| `/portfolios/{id}` | PUT | Update portfolio (e.g., name) | `{ "name": "string" }` | `{ "portfolioId": "string" }` |
| `/portfolios/user/{userId}` | GET | List portfolios for a user | - | `[{ "portfolioId": "string", "name": "string" }]` |
| `/portfolios/{id}/value` | GET | Get portfolio value | - | `{ "amount": double, "currency": "string" }` |

- **Example API Implementation** (Java with Spring WebFlux):
  ```java
  @RestController
  public class PortfolioController {
      @GetMapping("/portfolios/{id}/value")
      public Mono<PortfolioValue> value(@PathVariable String id) {
          return repository.findById(id)
              .map(p -> calculator.calculateValue(p, assetPrices));
      }
  }
  ```

## Integrations
The Portfolio Management Service interacts with other services and external systems:

- **Internal Integrations**:
  - **Asset Management Service**: Retrieves asset prices for valuation.
  - **Transaction Management Service**: Subscribes to "TradeExecuted" and "DividendPaid" events.
  - **Performance Calculation Service**: Provides portfolio data for performance metrics.
  - **Risk Management Service**: Supplies portfolio composition for diversification analysis.
  - **Reporting Service**: Sends portfolio data for dashboards and reports.

- **External Integrations**:
  - **Market Data Providers** (via Integration Service): Fetches real-time prices (e.g., Bloomberg, Reuters).
  - **Custodians** (via Integration Service): Syncs portfolio holdings with custodians like BNY Mellon.

- **Integration Mechanism**: Uses Kafka for event-driven communication and REST/gRPC for synchronous calls, with data fetched via the Integration Service.

## Technical Considerations
- **Database**: PostgreSQL for structured portfolio data, accessed via Slick for type-safe queries.
    @Entity
    @Table(name = "portfolios")
    public class PortfolioEntity {
        @Id String id;
        String userId;
        String name;
    }
  ```
 - **Event Sourcing**: Uses EventStoreDB to store events like "PortfolioUpdated" for auditability, integrated with Axon Framework.
 - **Security**: Implements OAuth2 (via Keycloak) for authentication and RBAC for user roles (e.g., investor, advisor).
 - **Scalability**: Deployed on Kubernetes with Spring Boot’s reactive stack for distributed processing.
 - **Performance**: Uses Project Reactor for efficient event processing and caching for frequent queries (e.g., portfolio value).
 - **Reliability**: Ensures fault tolerance via Spring Boot’s error handling and Kafka’s replication.
- **Maintainability**: FP principles (immutability, pure functions) and modular design simplify updates.

## Non-Functional Requirements Alignment
| **Requirement** | **How Addressed** |
|-----------------|-------------------|
| **Scalability** | Spring Boot’s reactive stack and Kubernetes scale the service to handle large portfolios and high request volumes. |
| **Security** | Keycloak and encryption (AES-256, TLS) ensure compliance with GDPR, MiFID II, and PCI DSS. |
| **Reliability** | Spring Boot error handling and PostgreSQL ACID transactions ensure 99.9% uptime. |
| **Performance** | Project Reactor and caching optimize real-time portfolio calculations. |
| **Maintainability** | FP principles and clear DDD modeling improve code clarity and testability.

## References
- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Apache Kafka](https://kafka.apache.org/documentation/)
- [DDD with Spring](https://spring.io/understanding/domain-driven-design)