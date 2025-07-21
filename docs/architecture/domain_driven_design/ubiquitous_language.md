## Overview

The ubiquitous_language.md document establishes a shared, consistent terminology for the Investment Portfolio Manager application, ensuring clear communication across all bounded contexts (e.g., Portfolio Management, Transaction Management). Aligned with DDD principles, it defines terms like "Portfolio," "Asset," and "Trade" to reflect the business domain of investment management. This document supports microservices, EDA, and FP by providing a common language for code, documentation, and discussions, reducing ambiguity and ensuring the system accurately represents the domain.

# Ubiquitous Language

The Ubiquitous Language is a shared vocabulary used across the Investment Portfolio Manager application to ensure consistency between the business domain, code, and documentation. Aligned with Domain-Driven Design (DDD) principles, this document defines key terms and their meanings within each bounded context, facilitating clear communication among developers, domain experts (e.g., financial advisors, portfolio managers), and stakeholders. The language is used consistently in microservices, Event-Driven Architecture (EDA), Functional Programming (FP) implementations, and documentation to reduce ambiguity and align the system with the domain of investment portfolio management.

## Purpose
- Ensure all team members (developers, business analysts, stakeholders) use a consistent terminology.
- Reflect the business domain accurately in code, APIs, events, and documentation.
- Support DDD by defining terms within bounded contexts (e.g., Portfolio Management, Asset Management).
- Facilitate collaboration across microservices, ensuring terms like "Portfolio" or "Trade" have clear, context-specific meanings.

## Bounded Contexts and Terms
The application is divided into bounded contexts, each with its own set of terms. Below are the key terms and their definitions, organized by bounded context.

### Portfolio Management
- **Portfolio**: A collection of assets owned by a user (individual investor, advisor, or institution), identified by a unique `portfolioId`. It tracks asset allocations and total value.
  - **Example**: A user’s "Retirement Fund" portfolio containing stocks, bonds, and ETFs.
- **Asset**: An investment instrument (e.g., stock, bond, real estate) held within a portfolio, identified by an `assetId` and associated with a quantity and currency.
  - **Example**: 100 shares of Apple stock (AAPL).
- **Portfolio Value**: The total monetary value of a portfolio, calculated as the sum of the values of its assets in a specified currency.
  - **Example**: $10,000 in USD.
- **Allocation**: The percentage of a portfolio’s value assigned to a specific asset or asset class.
  - **Example**: 30% allocated to equities.
- **Currency**: The monetary unit used for valuation and transactions (e.g., USD, EUR).
  - **Example**: A portfolio valued in EUR.

### Asset Management
- **Asset**: A financial instrument (e.g., equity, bond, ETF, real estate) with attributes like price, type, and currency.
  - **Example**: A corporate bond with a $1,000 face value and 5% coupon rate.
- **Asset Type**: The category of an asset (e.g., Equity, Fixed Income, Real Estate, Commodity).
  - **Example**: "Equity" for a stock like Microsoft (MSFT).
- **Price**: The current market value of an asset, typically per unit, in a specified currency.
  - **Example**: $150 per share of MSFT.
- **Dividend**: A payment made by an asset (e.g., stock) to its owner, typically in cash.
  - **Example**: $0.75 per share quarterly dividend for MSFT.
- **Corporate Action**: An event initiated by an asset’s issuer that affects its holders (e.g., stock split, merger).
  - **Example**: A 2-for-1 stock split.

### Transaction Management
- **Transaction**: A recorded action affecting a portfolio, such as buying or selling an asset, or receiving a dividend.
  - **Example**: Buying 50 shares of AAPL at $150 per share.
- **Trade**: A specific type of transaction involving the purchase or sale of an asset.
  - **Example**: A "Buy" trade for 100 shares of a stock.
- **Transaction Type**: The category of a transaction (e.g., Buy, Sell, Dividend Payment).
  - **Example**: "Sell" for disposing of an asset.
- **Amount**: The monetary or quantity value of a transaction.
  - **Example**: 100 shares or $1,000 in a trade.

### Performance Calculation
- **Return**: The gain or loss on a portfolio or asset, expressed as a percentage (e.g., total return, time-weighted return).
  - **Example**: A 5% annual return on a portfolio.
- **Benchmark**: A standard index or metric used to compare portfolio performance (e.g., S&P 500, MSCI World).
  - **Example**: Comparing portfolio returns to the S&P 500.
- **Performance Metric**: A calculated value measuring portfolio performance (e.g., total return, annualized return).
  - **Example**: A 7% annualized return.

### Risk Management
- **Risk Score**: A numerical value representing the risk level of a portfolio based on factors like volatility and diversification.
  - **Example**: A risk score of 4 out of 10.
- **Diversification**: The distribution of investments across asset classes or sectors to reduce risk.
  - **Example**: A portfolio with 40% equities, 40% bonds, and 20% real estate.
- **Stress Test**: A simulation to evaluate portfolio performance under adverse market conditions.
  - **Example**: Testing portfolio value during a 20% market crash.
- **Compliance Rule**: A regulatory or internal constraint on portfolio management (e.g., maximum exposure to a sector).
  - **Example**: Limiting equity exposure to 50% per MiFID II.

### Reporting
- **Report**: A structured document summarizing portfolio data, performance, or tax information.
  - **Example**: A capital gains report for tax filing.
- **Dashboard**: A customizable interface displaying key portfolio metrics (e.g., value, returns, risk).
  - **Example**: A user dashboard showing portfolio value and allocation.
- **Capital Gain/Loss**: The profit or loss from selling an asset, calculated as the difference between sale price and cost basis.
  - **Example**: A $1,000 capital gain from selling AAPL shares.

### User Management
- **User**: An individual or entity interacting with the system (e.g., investor, advisor, institutional client).
  - **Example**: An individual investor with a unique `userId`.
- **Role**: A set of permissions assigned to a user (e.g., Investor, Advisor, Administrator).
  - **Example**: An Advisor role with access to client portfolios.
- **Permission**: A specific action a user is allowed to perform (e.g., view portfolio, execute trades).
  - **Example**: Permission to update portfolio names.

### Integration
- **Market Data**: Real-time or historical data from external providers (e.g., Bloomberg, Reuters) for asset prices or exchange rates.
  - **Example**: Real-time price of AAPL at $150.
- **Custodian**: An external entity holding portfolio assets (e.g., BNY Mellon).
  - **Example**: A custodian reporting 100 shares of MSFT.
- **Exchange Rate**: The rate at which one currency is converted to another.
  - **Example**: 1 USD = 0.85 EUR.

## Usage Guidelines
- **Consistency**: Use these terms consistently in code (e.g., Java classes), APIs, event names, and documentation.
  - **Example**: A Java class for Portfolio:
    ```java
    public record Portfolio(portfolioId: String, userId: String, assets: List[Asset], name: String, createdAt: Date)
    ```
- **Context-Specific Meanings**: Some terms (e.g., "Asset") may have slightly different meanings in different bounded contexts. For example, in Asset Management, "Asset" includes pricing details, while in Portfolio Management, it focuses on quantity and allocation.
- **Documentation**: Reference this language in all Markdown files (e.g., `portfolio_management_service.md`) and ensure alignment in API contracts and event schemas.
- **Evolution**: Update this document as the domain evolves, using pull requests to track changes and maintain consensus among stakeholders.

## Examples in Context
- **Portfolio Management**:
  - A "Portfolio" is created for a "User" with a specific "Role" (e.g., Investor). The Portfolio contains multiple "Assets" with defined "Allocations" and is valued in a specific "Currency."
  - Event: `PortfolioUpdated` with fields `portfolioId` and `newValue` (PortfolioValue).
- **Transaction Management**:
  - A "Trade" (Transaction Type: Buy) adds an "Asset" to a "Portfolio," triggering a `TradeExecuted` event.
  - Code Example:
    ```java
    public record TradeExecuted(assetId: String, quantity: Double, transactionType: String)
    ```
- **Reporting**:
  - A "Report" for "Capital Gains/Losses" is generated from "Transactions" and displayed on a "Dashboard."

## References
- [Domain-Driven Design: Tackling Complexity in the Heart of Software](https://www.domainlanguage.com/ddd/)
- [Implementing Domain-Driven Design](https://www.baeldung.com/domain-driven-design)
- [Investment Portfolio Management Glossary](https://www.investopedia.com/terms/)


## Additional Notes:

- Storage: This document is stored in the GitHub repository at docs/architecture/domain_driven_design/ubiquitous_language.md, as per the defined folder structure.
- Purpose: The Ubiquitous Language ensures all microservices (e.g., Portfolio Management, Asset Management) use consistent terminology, reducing miscommunication. It is referenced in other documentation (e.g., portfolio_management_service.md) and code.
- Extensibility: The document can be updated as new terms or bounded contexts emerge, with changes tracked via GitHub pull requests.