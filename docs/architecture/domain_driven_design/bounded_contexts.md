# Overview
The bounded_contexts.md document outlines the bounded contexts of the Investment Portfolio Manager application, each representing a distinct area of the domain with its own model, terminology, and responsibilities. These contexts align with the microservices architecture, ensuring loose coupling and clear boundaries. The document supports DDD by defining how each context interacts with others through APIs, events, or shared models, and it leverages EDA for real-time communication and FP for reliable computations. This ensures the system accurately reflects the investment management domain while meeting non-functional requirements like scalability, security, and maintainability.

# Bounded Contexts

The Investment Portfolio Manager application is structured using Domain-Driven Design (DDD), with each bounded context defining a specific area of the domain with its own model, ubiquitous language, and responsibilities. These contexts align with the microservices architecture, ensuring modularity, loose coupling, and clear boundaries. Each bounded context corresponds to a microservice, interacts through APIs or events (via Event-Driven Architecture), and uses Scala with Functional Programming (FP) principles for reliable computations. This document outlines each bounded context, its purpose, key entities, and relationships, supporting the system’s scalability, security, reliability, and maintainability.

## Purpose
- Define clear boundaries for each microservice’s domain model to prevent overlap and ensure consistency.
- Align with the ubiquitous language (see `ubiquitous_language.md`) to maintain a shared vocabulary.
- Facilitate communication between microservices through APIs, events, or shared models.
- Support regulatory compliance (e.g., GDPR, MiFID II, GIPS) by isolating concerns like compliance rules or audit trails.

## Bounded Contexts

### 1. Portfolio Management
- **Purpose**: Manages the creation, updating, and tracking of portfolios, including asset allocations and portfolio-level metrics (e.g., total value).
- **Microservice**: Portfolio Management Service
- **Key Entities and Aggregates**:
  - **Portfolio** (Aggregate Root): Represents a user’s collection of assets, identified by `portfolioId`. Includes attributes like `userId`, `assets` (List of Asset), `name`, and `createdAt`.
  - **Asset**: An investment instrument within a portfolio, with attributes like `assetId`, `quantity`, and `currency`.
- **Value Objects**:
  - **Currency**: Represents the monetary unit (e.g., USD, EUR).
  - **Percentage**: Represents allocation weights (e.g., 30% equities).
  - **PortfolioValue**: Represents the total value of the portfolio in a currency.
- **Responsibilities**:
  - Create, update, and delete portfolios.
  - Track asset allocations and calculate portfolio value.
  - Enforce investment constraints (e.g., diversification rules).
- **Interactions**:
  - **Subscribes to Events**: `TradeExecuted`, `DividendPaid` (from Transaction Management), `PriceUpdated` (from Asset Management).
  - **Publishes Events**: `PortfolioUpdated` (e.g., after asset changes).
  - **APIs**: Provides endpoints like `/portfolios/{id}` to retrieve portfolio details.
  - **External Systems**: Syncs with custodians (via Integration Service) for holding data.
- **Example**:
  ```scala
  case class Portfolio(portfolioId: String, userId: String, assets: List[Asset], name: String, createdAt: Date)
  ```

### 2. Asset Management
- **Purpose**: Manages asset types (e.g., equities, bonds, real estate) and their pricing, providing data for portfolio valuation and analysis.
- **Microservice**: Asset Management Service
- **Key Entities and Aggregates**:
  - **Asset** (Aggregate Root): Represents a financial instrument, with attributes like `assetId`, `type`, `price`, and `currency`.
- **Value Objects**:
  - **AssetType**: Represents the category (e.g., Equity, Fixed Income).
  - **Price**: Represents the current market value of an asset.
- **Responsibilities**:
  - Maintain asset metadata and real-time prices.
  - Handle corporate actions (e.g., stock splits, dividends).
- **Interactions**:
  - **Subscribes to Events**: `MarketDataUpdated` (from Integration Service).
  - **Publishes Events**: `PriceUpdated`, `CorporateActionApplied`.
  - **APIs**: Provides endpoints like `/assets/{id}/price` for price queries.
  - **External Systems**: Integrates with market data providers (e.g., Bloomberg) via Integration Service.
- **Example**:
  ```scala
  case class Asset(assetId: String, assetType: AssetType, price: Price, currency: Currency)
  ```

### 3. Transaction Management
- **Purpose**: Records and processes transactions (e.g., buy/sell orders, dividends) affecting portfolios.
- **Microservice**: Transaction Management Service
- **Key Entities and Aggregates**:
  - **Transaction** (Aggregate Root): Represents a transaction, with attributes like `transactionId`, `assetId`, `quantity`, `transactionType`, and `date`.
- **Value Objects**:
  - **TransactionType**: Represents the type (e.g., Buy, Sell, Dividend).
  - **Amount**: Represents the monetary or quantity value of a transaction.
- **Responsibilities**:
  - Record buy/sell orders, dividends, and other transactions.
  - Validate transactions against portfolio constraints.
- **Interactions**:
  - **Subscribes to Events**: `OrderPlaced` (from external brokers via Integration Service).
  - **Publishes Events**: `TradeExecuted`, `DividendPaid`.
  - **APIs**: Provides endpoints like `/transactions/{id}` for transaction details.
  - **External Systems**: Integrates with brokers for trade execution.
- **Example**:
  ```scala
  case class Transaction(transactionId: String, assetId: String, quantity: Double, transactionType: TransactionType, date: Date)
  ```

### 4. Performance Calculation
- **Purpose**: Computes portfolio performance metrics (e.g., total return, benchmark comparison).
- **Microservice**: Performance Calculation Service
- **Key Entities and Aggregates**:
  - None (primarily uses domain services and value objects).
- **Value Objects**:
  - **Return**: Represents performance gain/loss (e.g., 5% annual return).
  - **Benchmark**: Represents a standard index (e.g., S&P 500).
  - **PerformanceMetric**: Represents calculated metrics (e.g., annualized return).
- **Responsibilities**:
  - Calculate returns (e.g., time-weighted, total return).
  - Compare portfolio performance against benchmarks.
- **Interactions**:
  - **Subscribes to Events**: `PortfolioUpdated`, `PriceUpdated`.
  - **Publishes Events**: `PerformanceCalculated`.
  - **APIs**: Provides endpoints like `/portfolios/{id}/performance` for metrics.
  - **External Systems**: Uses benchmark data (via Integration Service).
- **Example**:
  ```scala
  case class PerformanceMetric(totalReturn: Double, benchmarkReturn: Double)
  ```

### 5. Risk Management
- **Purpose**: Analyzes portfolio risk, including diversification, stress testing, and regulatory compliance.
- **Microservice**: Risk Management Service
- **Key Entities and Aggregates**:
  - None (primarily uses domain services and value objects).
- **Value Objects**:
  - **RiskScore**: Represents the portfolio’s risk level.
  - **StressTestScenario**: Represents adverse market conditions.
  - **ComplianceRule**: Represents regulatory or internal constraints.
- **Responsibilities**:
  - Calculate risk metrics (e.g., beta, standard deviation).
  - Perform stress tests (e.g., market crash scenarios).
  - Ensure compliance with regulations (e.g., MiFID II).
- **Interactions**:
  - **Subscribes to Events**: `PortfolioUpdated`, `PriceUpdated`.
  - **Publishes Events**: `RiskAssessmentUpdated`, `ComplianceViolationDetected`.
  - **APIs**: Provides endpoints like `/portfolios/{id}/risk` for risk metrics.
  - **External Systems**: Integrates with regulatory reporting systems.
- **Example**:
  ```scala
  case class RiskScore(value: Double)
  ```

### 6. Reporting
- **Purpose**: Generates reports and dashboards for portfolio performance, tax, and capital gains/losses.
- **Microservice**: Reporting Service
- **Key Entities and Aggregates**:
  - **Report** (Aggregate Root): Represents a report, with attributes like `reportId`, `type`, and `dateRange`.
- **Value Objects**:
  - **ReportType**: Represents the report category (e.g., Performance, Tax).
  - **CapitalGainLoss**: Represents profit/loss from asset sales.
- **Responsibilities**:
  - Generate performance, tax, and capital gains/losses reports.
  - Provide customizable dashboards for users.
- **Interactions**:
  - **Subscribes to Events**: `PortfolioUpdated`, `PerformanceCalculated`.
  - **Publishes Events**: `ReportGenerated`.
  - **APIs**: Provides endpoints like `/reports/{id}` for report retrieval.
  - **External Systems**: Exports reports to tax software.
- **Example**:
  ```scala
  case class Report(reportId: String, reportType: ReportType, dateRange: DateRange)
  ```

### 7. User Management
- **Purpose**: Manages user accounts, roles, and permissions.
- **Microservice**: User Management Service
- **Key Entities and Aggregates**:
  - **User** (Aggregate Root): Represents a user, with attributes like `userId`, `role`, and `permissions`.
- **Value Objects**:
  - **Role**: Represents a user role (e.g., Investor, Advisor).
  - **Permission**: Represents allowed actions (e.g., view portfolio).
- **Responsibilities**:
  - Manage user authentication and authorization.
  - Assign roles and permissions.
- **Interactions**:
  - **Subscribes to Events**: None (primarily synchronous).
  - **Publishes Events**: `UserCreated`, `RoleUpdated`.
  - **APIs**: Provides endpoints like `/users/{id}` for user details.
  - **External Systems**: Integrates with Keycloak for authentication.
- **Example**:
  ```scala
  case class User(userId: String, role: Role, permissions: List[Permission])
  ```

### 8. Integration
- **Purpose**: Manages connections with external systems (e.g., market data providers, custodians, brokers).
- **Microservice**: Integration Service
- **Key Entities and Aggregates**:
  - None (primarily uses domain services and value objects).
- **Value Objects**:
  - **MarketData**: Represents external data (e.g., asset prices, exchange rates).
  - **CustodianData**: Represents holding data from custodians.
- **Responsibilities**:
  - Fetch real-time market data and exchange rates.
  - Sync portfolio holdings with custodians and execute trades via brokers.
- **Interactions**:
  - **Subscribes to Events**: None (primarily synchronous).
  - **Publishes Events**: `MarketDataUpdated`, `CustodianDataSynced`.
  - **APIs**: Provides endpoints like `/market-data/{assetId}` for external data.
  - **External Systems**: Connects to Bloomberg, Reuters, BNY Mellon, etc.
- **Example**:
  ```scala
  case class MarketData(assetId: String, price: Double, currency: Currency)
  ```

## Context Mapping
The bounded contexts interact through APIs, events, or shared models, as defined below:

| **Bounded Context** | **Interacts With** | **Interaction Type** | **Details** |
|---------------------|--------------------|----------------------|-------------|
| Portfolio Management | Asset Management, Transaction Management, Performance Calculation, Risk Management, Reporting | Events, APIs | Subscribes to `TradeExecuted`, `PriceUpdated`; publishes `PortfolioUpdated`. |
| Asset Management | Integration, Portfolio Management | Events, APIs | Publishes `PriceUpdated`; uses `MarketDataUpdated`. |
| Transaction Management | Portfolio Management, Integration | Events, APIs | Publishes `TradeExecuted`, `DividendPaid`. |
| Performance Calculation | Portfolio Management, Asset Management | Events, APIs | Subscribes to `PortfolioUpdated`, `PriceUpdated`. |
| Risk Management | Portfolio Management, Asset Management | Events, APIs | Subscribes to `PortfolioUpdated`; publishes `RiskAssessmentUpdated`. |
| Reporting | Portfolio Management, Performance Calculation | Events, APIs | Subscribes to `PortfolioUpdated`, `PerformanceCalculated`. |
| User Management | All contexts | APIs | Provides user authentication/authorization via Keycloak. |
| Integration | All contexts | Events, APIs | Publishes `MarketDataUpdated`, `CustodianDataSynced`. |

- **Patterns Used**:
  - **Open Host Service (OHS)**: Each context exposes a public API (e.g., REST via Akka HTTP) for synchronous interactions.
  - **Published Language (PL)**: Events (e.g., `TradeExecuted`) use a standardized format across contexts, aligned with the ubiquitous language.
  - **Customer/Supplier**: Upstream contexts (e.g., Integration) supply data to downstream contexts (e.g., Asset Management).

## Guidelines
- **Consistency**: Use the ubiquitous language (see `ubiquitous_language.md`) within each bounded context to ensure terminology aligns with the domain model.
- **Isolation**: Each context maintains its own database (e.g., PostgreSQL for Portfolio Management, MongoDB for Reporting) to ensure loose coupling.
- **Event-Driven Communication**: Use Apache Kafka for asynchronous event handling, with EventStoreDB for event sourcing in contexts like Transaction Management.
- **Documentation**: Reference these bounded contexts in microservice-specific files (e.g., `portfolio_management_service.md`) and update as the domain evolves.
- **Compliance**: Ensure contexts like Risk Management and Reporting incorporate regulatory checks (e.g., MiFID II, GIPS).

## References
- [Domain-Driven Design: Tackling Complexity in the Heart of Software](https://www.domainlanguage.com/ddd/)
- [Implementing Domain-Driven Design with Scala](https://www.lightbend.com/blog/domain-driven-design-with-scala)
- [Microservices Context Mapping](https://microservices.io/patterns/refactoring/context-mapping.html)

## Additional Notes:

- Storage: This document is stored in the GitHub repository at docs/architecture/domain_driven_design/bounded_contexts.md, as per the defined folder structure.
- Purpose: The document ensures each microservice operates within a well-defined bounded context, reducing overlap and aligning with DDD principles. It complements the ubiquitous_language.md by providing context-specific details.
- Extensibility: The document can be updated as new bounded contexts or interactions emerge, with changes tracked via GitHub pull requests.
