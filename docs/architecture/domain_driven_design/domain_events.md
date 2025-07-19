## Overview
The domain_events.md document outlines the domain events used in the Investment Portfolio Manager application, which facilitate communication between microservices and enable real-time updates in response to business activities. Aligned with DDD and EDA principles, these events represent significant changes in the domain (e.g., a trade being executed or an asset price updating). Implemented in Java with reactive principles, the events ensure immutability and reliability. The document details each event’s structure, purpose, and interactions across bounded contexts, supporting non-functional requirements like scalability, reliability, and compliance.

# Domain Events

Domain events are a core component of the Investment Portfolio Manager application’s Event-Driven Architecture (EDA), capturing significant business activities within the domain as defined by Domain-Driven Design (DDD). These events enable asynchronous communication between microservices, ensuring loose coupling and real-time responsiveness. Implemented in Java with Project Reactor for non-blocking handling, events are published and consumed via Apache Kafka and stored in EventStoreDB for auditability. This document lists all 16 domain events, their structure, purpose, and interactions across bounded contexts, supporting non-functional requirements like scalability, reliability, performance, and regulatory compliance (e.g., GDPR, MiFID II, GIPS).

## Purpose
- Capture significant business activities (e.g., trade execution, price updates, rebalancing) as immutable events.
- Enable asynchronous communication between bounded contexts (e.g., Portfolio Management, Transaction Management).
- Support event sourcing for auditability and state reconstruction, critical for financial systems.
- Align with the ubiquitous language (see `ubiquitous_language.md`) to ensure consistency with the domain model.

## Domain Events

Each event is defined with its name, structure, purpose, publishing context, subscribing contexts, and an example implementation in Java. Events are published to Apache Kafka topics and stored in EventStoreDB for compliance and auditing.

### 1. TradeExecuted
- **Purpose**: Indicates that a buy or sell order has been successfully executed, triggering updates to portfolios or risk assessments.
- **Bounded Context (Publisher)**: Transaction Management
- **Bounded Context (Subscribers)**: Portfolio Management, Performance Calculation, Risk Management
- **Structure**:
  - `transactionId: String` (unique identifier for the transaction)
  - `portfolioId: String` (portfolio affected by the trade)
  - `assetId: String` (asset being traded)
  - `quantity: Double` (number of units traded)
  - `transactionType: String` (e.g., "BUY", "SELL")
  - `price: Double` (price per unit at execution)
  - `currency: Currency` (currency of the trade)
  - `timestamp: Date` (execution time)
- **Example**:
  ```scala
  case class TradeExecuted(
    transactionId: String,
    portfolioId: String,
    assetId: String,
    quantity: Double,
    transactionType: String,
    price: Double,
    currency: Currency,
    timestamp: Date
  )
  ```
- **Actions**:
  - Portfolio Management: Updates the portfolio’s asset list.
  - Performance Calculation: Recalculates portfolio returns.
  - Risk Management: Reassesses portfolio risk exposure.
- **Kafka Topic**: `trade-executed`

### 2. PriceUpdated
- **Purpose**: Signals a change in an asset’s market price, triggering updates to portfolio valuations or risk metrics.
- **Bounded Context (Publisher)**: Asset Management
- **Bounded Context (Subscribers)**: Portfolio Management, Performance Calculation, Risk Management
- **Structure**:
  - `assetId: String` (unique identifier for the asset)
  - `price: Double` (new market price)
  - `currency: Currency` (currency of the price)
  - `timestamp: Date` (time of price update)
- **Example**:
  ```scala
  case class PriceUpdated(
    assetId: String,
    price: Double,
    currency: Currency,
    timestamp: Date
  )
  ```
- **Actions**:
  - Portfolio Management: Recalculates portfolio value.
  - Performance Calculation: Updates performance metrics.
  - Risk Management: Adjusts risk calculations based on new prices.
- **Kafka Topic**: `price-updated`

### 3. DividendPaid
- **Purpose**: Indicates a dividend payment for an asset, affecting portfolio cash holdings or reinvestment.
- **Bounded Context (Publisher)**: Transaction Management
- **Bounded Context (Subscribers)**: Portfolio Management, Performance Calculation
- **Structure**:
  - `transactionId: String` (unique identifier for the dividend transaction)
  - `portfolioId: String` (portfolio receiving the dividend)
  - `assetId: String` (asset paying the dividend)
  - `amount: Double` (dividend amount)
  - `currency: Currency` (currency of the dividend)
  - `timestamp: Date` (payment time)
- **Example**:
  ```scala
  case class DividendPaid(
    transactionId: String,
    portfolioId: String,
    assetId: String,
    amount: Double,
    currency: Currency,
    timestamp: Date
  )
  ```
- **Actions**:
  - Portfolio Management: Updates cash holdings or reinvests dividends.
  - Performance Calculation: Adjusts return calculations.
- **Kafka Topic**: `dividend-paid`

### 4. PortfolioUpdated
- **Purpose**: Signals a change in a portfolio’s state (e.g., after a trade or dividend), triggering reporting or performance updates.
- **Bounded Context (Publisher)**: Portfolio Management
- **Bounded Context (Subscribers)**: Performance Calculation, Risk Management, Reporting
- **Structure**:
  - `portfolioId: String` (unique identifier for the portfolio)
  - `newValue: PortfolioValue` (updated portfolio value)
  - `timestamp: Date` (update time)
- **Example**:
  ```scala
  case class PortfolioUpdated(
    portfolioId: String,
    newValue: PortfolioValue,
    timestamp: Date
  )
  case class PortfolioValue(amount: Double, currency: Currency)
  ```
- **Actions**:
  - Performance Calculation: Recalculates performance metrics.
  - Risk Management: Reassesses risk exposure.
  - Reporting: Updates dashboards or reports.
- **Kafka Topic**: `portfolio-updated`

### 5. PerformanceCalculated
- **Purpose**: Indicates that new performance metrics for a portfolio have been calculated, triggering reporting updates.
- **Bounded Context (Publisher)**: Performance Calculation
- **Bounded Context (Subscribers)**: Reporting
- **Structure**:
  - `portfolioId: String` (portfolio being measured)
  - `totalReturn: Double` (portfolio return percentage)
  - `benchmarkReturn: Double` (benchmark return for comparison)
  - `timestamp: Date` (calculation time)
- **Example**:
  ```scala
  case class PerformanceCalculated(
    portfolioId: String,
    totalReturn: Double,
    benchmarkReturn: Double,
    timestamp: Date
  )
  ```
- **Actions**:
  - Reporting: Updates performance reports and dashboards.
- **Kafka Topic**: `performance-calculated`

### 6. RiskAssessmentUpdated
- **Purpose**: Signals an updated risk assessment for a portfolio, triggering compliance checks or reporting.
- **Bounded Context (Publisher)**: Risk Management
- **Bounded Context (Subscribers)**: Reporting
- **Structure**:
  - `portfolioId: String` (portfolio being assessed)
  - `riskScore: Double` (updated risk score)
  - `timestamp: Date` (assessment time)
- **Example**:
  ```scala
  case class RiskAssessmentUpdated(
    portfolioId: String,
    riskScore: Double,
    timestamp: Date
  )
  ```
- **Actions**:
  - Reporting: Updates risk-related dashboards or reports.
- **Kafka Topic**: `risk-assessment-updated`

### 7. ComplianceViolationDetected
- **Purpose**: Indicates a violation of a regulatory or internal compliance rule (e.g., excessive sector exposure).
- **Bounded Context (Publisher)**: Risk Management
- **Bounded Context (Subscribers)**: Reporting, Portfolio Management
- **Structure**:
  - `portfolioId: String` (portfolio with the violation)
  - `ruleId: String` (identifier of the violated rule)
  - `description: String` (details of the violation)
  - `timestamp: Date` (detection time)
- **Example**:
  ```scala
  case class ComplianceViolationDetected(
    portfolioId: String,
    ruleId: String,
    description: String,
    timestamp: Date
  )
  ```
- **Actions**:
  - Reporting: Generates compliance alerts or reports.
  - Portfolio Management: Triggers review or rebalancing.
- **Kafka Topic**: `compliance-violation-detected`

### 8. ReportGenerated
- **Purpose**: Signals that a new report (e.g., performance, tax) has been generated, triggering delivery to users.
- **Bounded Context (Publisher)**: Reporting
- **Bounded Context (Subscribers)**: None (primarily consumed by external systems or users)
- **Structure**:
  - `reportId: String` (unique identifier for the report)
  - `portfolioId: String` (portfolio associated with the report)
  - `reportType: String` (e.g., "Performance", "Tax")
  - `timestamp: Date` (generation time)
- **Example**:
  ```scala
  case class ReportGenerated(
    reportId: String,
    portfolioId: String,
    reportType: String,
    timestamp: Date
  )
  ```
- **Actions**:
  - External Systems: Delivers reports to users or tax software.
- **Kafka Topic**: `report-generated`

### 9. UserCreated
- **Purpose**: Indicates the creation of a new user account, triggering portfolio initialization or permission setup.
- **Bounded Context (Publisher)**: User Management
- **Bounded Context (Subscribers)**: Portfolio Management
- **Structure**:
  - `userId: String` (unique identifier for the user)
  - `role: String` (e.g., "Investor", "Advisor")
  - `timestamp: Date` (creation time)
- **Example**:
  ```scala
  case class UserCreated(
    userId: String,
    role: String,
    timestamp: Date
  )
  ```
- **Actions**:
  - Portfolio Management: Initializes a default portfolio for the user.
- **Kafka Topic**: `user-created`

### 10. MarketDataUpdated
- **Purpose**: Signals updated market data (e.g., asset prices, exchange rates) from external providers.
- **Bounded Context (Publisher)**: Integration
- **Bounded Context (Subscribers)**: Asset Management
- **Structure**:
  - `assetId: String` (asset or currency pair)
  - `value: Double` (new price or exchange rate)
  - `currency: Currency` (currency of the value)
  - `timestamp: Date` (update time)
- **Example**:
  ```scala
  case class MarketDataUpdated(
    assetId: String,
    value: Double,
    currency: Currency,
    timestamp: Date
  )
  ```
- **Actions**:
  - Asset Management: Updates asset prices.
- **Kafka Topic**: `market-data-updated`

### 11. CustodianDataSynced
- **Purpose**: Indicates that portfolio holdings have been synchronized with a custodian, ensuring data accuracy.
- **Bounded Context (Publisher)**: Integration
- **Bounded Context (Subscribers)**: Portfolio Management
- **Structure**:
  - `portfolioId: String` (portfolio being synced)
  - `assets: List[Asset]` (updated holdings)
  - `timestamp: Date` (sync time)
- **Example**:
  ```scala
  case class CustodianDataSynced(
    portfolioId: String,
    assets: List[Asset],
    timestamp: Date
  )
  case class Asset(assetId: String, quantity: Double, currency: Currency)
  ```
- **Actions**:
  - Portfolio Management: Updates portfolio assets to match custodian data.
- **Kafka Topic**: `custodian-data-synced`

### 12. CorporateActionApplied
- **Purpose**: Captures corporate actions beyond dividends (e.g., stock splits, mergers) that affect asset quantities or portfolio composition.
- **Bounded Context (Publisher)**: Asset Management
- **Bounded Context (Subscribers)**: Portfolio Management, Transaction Management
- **Structure**:
  - `assetId: String` (asset affected by the action)
  - `actionType: String` (e.g., "STOCK_SPLIT", "MERGER")
  - `details: Map[String, String]` (e.g., {"ratio": "2:1"})
  - `timestamp: Date` (action time)
- **Example**:
  ```scala
  case class CorporateActionApplied(
    assetId: String,
    actionType: String,
    details: Map[String, String],
    timestamp: Date
  )
  ```
- **Actions**:
  - Portfolio Management: Adjusts asset quantities (e.g., doubling shares for a 2:1 split).
  - Transaction Management: Records the action as a transaction for auditing.
- **Kafka Topic**: `corporate-action-applied`

### 13. OrderPlaced
- **Purpose**: Signals that a trade order has been submitted to a broker, before execution, to track pending transactions.
- **Bounded Context (Publisher)**: Integration
- **Bounded Context (Subscribers)**: Transaction Management
- **Structure**:
  - `orderId: String` (unique identifier for the order)
  - `portfolioId: String` (portfolio initiating the order)
  - `assetId: String` (asset being traded)
  - `quantity: Double` (number of units)
  - `orderType: String` (e.g., "BUY", "SELL")
  - `timestamp: Date` (order submission time)
- **Example**:
  ```scala
  case class OrderPlaced(
    orderId: String,
    portfolioId: String,
    assetId: String,
    quantity: Double,
    orderType: String,
    timestamp: Date
  )
  ```
- **Actions**:
  - Transaction Management: Tracks pending orders and reconciles with `TradeExecuted`.
- **Kafka Topic**: `order-placed`

### 14. UserUpdated
- **Purpose**: Indicates changes to a user’s account (e.g., role or permission updates), affecting portfolio access or compliance.
- **Bounded Context (Publisher)**: User Management
- **Bounded Context (Subscribers)**: Portfolio Management, Reporting
- **Structure**:
  - `userId: String` (unique identifier for the user)
  - `role: String` (updated role, e.g., "Investor", "Advisor")
  - `permissions: List[String]` (updated permissions)
  - `timestamp: Date` (update time)
- **Example**:
  ```scala
  case class UserUpdated(
    userId: String,
    role: String,
    permissions: List[String],
    timestamp: Date
  )
  ```
- **Actions**:
  - Portfolio Management: Updates access permissions for portfolios.
  - Reporting: Adjusts user-specific dashboards or reports.
- **Kafka Topic**: `user-updated`

### 15. ComplianceRuleUpdated
- **Purpose**: Signals updates to regulatory or internal compliance rules (e.g., new MiFID II requirements), triggering re-evaluations.
- **Bounded Context (Publisher)**: Risk Management
- **Bounded Context (Subscribers)**: Portfolio Management, Reporting
- **Structure**:
  - `ruleId: String` (unique identifier for the rule)
  - `description: String` (rule details)
  - `constraints: Map[String, String]` (e.g., {"maxEquityExposure": "50%"})
  - `timestamp: Date` (update time)
- **Example**:
  ```scala
  case class ComplianceRuleUpdated(
    ruleId: String,
    description: String,
    constraints: Map[String, String],
    timestamp: Date
  )
  ```
- **Actions**:
  - Portfolio Management: Triggers portfolio rebalancing to meet new rules.
  - Reporting: Updates compliance reports.
- **Kafka Topic**: `compliance-rule-updated`

### 16. PortfolioRebalanced
- **Purpose**: Indicates that a portfolio has been rebalanced to meet allocation or risk targets.
- **Bounded Context (Publisher)**: Portfolio Management
- **Bounded Context (Subscribers)**: Performance Calculation, Risk Management, Reporting
- **Structure**:
  - `portfolioId: String` (portfolio being rebalanced)
  - `newAllocations: Map[String, Percentage]` (updated allocations, e.g., {"Equities": 30%})
  - `timestamp: Date` (rebalance time)
- **Example**:
  ```scala
  case class PortfolioRebalanced(
    portfolioId: String,
    newAllocations: Map[String, Percentage],
    timestamp: Date
  )
  case class Percentage(value: Double)
  ```
- **Actions**:
  - Performance Calculation: Recalculates performance metrics.
  - Risk Management: Reassesses risk exposure.
  - Reporting: Updates dashboards or reports.
- **Kafka Topic**: `portfolio-rebalanced`

## Event Handling Guidelines
- **Immutability**: Events are implemented as immutable value objects in Java, ensuring reliability and thread safety.
- **Event Sourcing**: Events are stored in EventStoreDB for auditability and state reconstruction, critical for compliance (e.g., MiFID II, GIPS).
- **Idempotency**: Event handlers are designed to handle duplicate events gracefully, using `transactionId`, `orderId`, or `eventId` for deduplication.
- **Schema Consistency**: Event structures align with the ubiquitous language (see `ubiquitous_language.md`) and are versioned to support evolution.
- **Kafka Configuration**: Events are published to specific Kafka topics with partitioning for scalability and replication for reliability.
- **Error Handling**: Use Spring Boot’s error handling mechanisms to manage failures in event processing.

## Example Event Flow
1. A user submits a trade order:
   - Integration Service publishes `OrderPlaced` to the `order-placed` topic.
   - Transaction Management Service subscribes, processes the order, and publishes `TradeExecuted`.
   - Portfolio Management Service subscribes to `TradeExecuted`, updates the portfolio, and publishes `PortfolioUpdated`.
   - Performance Calculation Service subscribes to `PortfolioUpdated`, recalculates metrics, and publishes `PerformanceCalculated`.
   - Reporting Service subscribes to `PerformanceCalculated`, updates dashboards, and publishes `ReportGenerated`.

## Non-Functional Requirements Alignment
| **Requirement** | **How Addressed** |
|-----------------|-------------------|
| **Scalability** | Kafka’s partitioning and Project Reactor handle high event volumes across microservices. |
| **Reliability** | Kafka replication and EventStoreDB ensure event delivery and auditability. |
| **Performance** | Project Reactor and Kafka optimize low-latency event processing. |
| **Security** | Events are encrypted in transit (TLS) and access-controlled via Keycloak. |
| **Maintainability** | Immutable event structures and clear documentation simplify updates. |

## References
- [Domain-Driven Design: Tackling Complexity in the Heart of Software](https://www.domainlanguage.com/ddd/)
- [Event-Driven Architecture with Apache Kafka](https://kafka.apache.org/documentation/#introduction)
- [Event Sourcing with EventStoreDB](https://www.eventstore.com/docs/)
- [Project Reactor](https://projectreactor.io/)

## Additional Notes:

- Storage: This document is stored in the GitHub repository at docs/architecture/domain_driven_design/domain_events.md, as per the defined folder structure.
- Purpose: The document ensures that domain events are clearly defined and aligned with the ubiquitous language and bounded contexts (see ubiquitous_language.md and bounded_contexts.md), facilitating EDA implementation.
- Extensibility: New events can be added as the domain evolves, with changes tracked via GitHub pull requests.