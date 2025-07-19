# Portfolio Management Service Specification

The Portfolio Management Service is a core microservice in the Investment Portfolio Manager application, responsible for managing portfolios within the Portfolio Management bounded context. It handles the creation, updating, and tracking of portfolios, including asset allocations and total valuation, ensuring alignment with the domain model and ubiquitous language (see `ubiquitous_language.md`). Built using **Java** with Spring Boot, following Domain-Driven Design (DDD) and Event-Driven Architecture (EDA) principles, the service leverages Spring WebFlux for HTTP APIs and reactive event processing, Spring Data JPA for PostgreSQL persistence, and Apache Kafka for event-driven communication. It integrates with other microservices (e.g., Transaction Management, Asset Management) to support real-time updates, regulatory compliance (e.g., GDPR, MiFID II), and non-functional requirements like scalability, reliability, and performance.

## Responsibilities
- **Portfolio Management**:
  - Create, update, and delete portfolios for users (e.g., investors, advisors).
  - Track asset allocations (e.g., percentage of equities, bonds).
  - Calculate portfolio value in a specified currency based on asset prices.
- **Constraint Enforcement**:
  - Enforce investment constraints (e.g., diversification rules, maximum sector exposure).
  - Validate updates against compliance rules (e.g., MiFID II limits).
- **Event Processing**:
  - Handle events like `TradeExecuted`, `DividendPaid`, `PriceUpdated`, `CustodianDataSynced`, and `CorporateActionApplied` to update portfolios.
  - Publish events like `PortfolioUpdated` and `PortfolioRebalanced` to notify downstream services.
- **Integration**:
  - Sync portfolio holdings with external custodians (via Integration Service).
  - Retrieve real-time asset prices (via Asset Management Service).
- **Auditability**:
  - Store portfolio state changes in EventStoreDB for compliance and auditing.

## Bounded Context
The Portfolio Management Service operates within the **Portfolio Management** bounded context (see `bounded_contexts.md`), which focuses on managing portfolios and their assets. It interacts with other bounded contexts via events and APIs:
- **Transaction Management**: Subscribes to `TradeExecuted` and `DividendPaid` to update portfolio assets and cash.
- **Asset Management**: Subscribes to `PriceUpdated` and `CorporateActionApplied` for asset price and corporate action updates.
- **Integration**: Subscribes to `CustodianDataSynced` for external holding synchronization.
- **Performance Calculation, Risk Management, Reporting**: Publishes `PortfolioUpdated` and `PortfolioRebalanced` to trigger downstream calculations and reports.

## Domain Model
The domain model is implemented as immutable Java records, adhering to DDD and FP principles. Key entities and value objects are defined below, aligned with the ubiquitous language.

### Entities and Aggregates
- **Portfolio (Aggregate Root)**:
  - Represents a collection of assets owned by a user.
  - Attributes:
    - `portfolioId: String` (unique identifier)
    - `userId: String` (owner of the portfolio)
    - `assets: List[Asset]` (list of assets held)
    - `name: String` (e.g., "Retirement Fund")
    - `createdAt: Date` (creation timestamp)
  - Example:
    ```scala
    case class Portfolio(portfolioId: String, userId: String, assets: List[Asset], name: String, createdAt: Date)
    ```

- **Asset**:
  - Represents an investment instrument within a portfolio.
  - Attributes:
    - `assetId: String` (unique identifier, e.g., AAPL)
    - `quantity: Double` (number of units held)
    - `currency: Currency` (valuation currency)
  - Example:
    ```scala
    case class Asset(assetId: String, quantity: Double, currency: Currency)
    ```

### Value Objects
- **Currency**:
  - Represents the monetary unit (e.g., USD, EUR).
  - Attributes:
    - `code: String` (e.g., "USD")
  - Example:
    ```scala
    case class Currency(code: String)
    ```

- **PortfolioValue**:
  - Represents the total monetary value of a portfolio.
  - Attributes:
    - `amount: Double` (value in currency)
    - `currency: Currency` (valuation currency)
  - Example:
    ```scala
    case class PortfolioValue(amount: Double, currency: Currency)
    ```

- **Percentage**:
  - Represents allocation weights (e.g., 30% equities).
  - Attributes:
    - `value: Double` (percentage, 0-100)
  - Example:
    ```scala
    case class Percentage(value: Double)
    ```

## API Endpoints
The service exposes RESTful APIs using Spring WebFlux, hosted at `/portfolios`. All endpoints are secured with Keycloak for authentication and authorization.

| **Endpoint** | **Method** | **Description** | **Request Body** | **Response** |
|--------------|------------|-----------------|------------------|--------------|
| `/portfolios` | POST | Creates a new portfolio | `{"userId": "u1", "name": "Retirement Fund"}` | `Portfolio` (JSON) |
| `/portfolios/{portfolioId}` | GET | Retrieves portfolio details | - | `Portfolio` (JSON) |
| `/portfolios/{portfolioId}` | PUT | Updates portfolio (e.g., name) | `{"name": "Updated Fund"}` | `Portfolio` (JSON) |
| `/portfolios/{portfolioId}` | DELETE | Deletes a portfolio | - | 204 No Content |
| `/portfolios/{portfolioId}/value` | GET | Retrieves portfolio value | - | `PortfolioValue` (JSON) |
| `/portfolios/{portfolioId}/allocations` | GET | Retrieves asset allocations | - | `Map[String, Percentage]` (JSON) |

### Example API Implementation
```scala
// src/portfolio-management-service/main/scala/api/PortfolioRoutes.scala
package com.example.portfolio.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import com.example.portfolio.domain.{Portfolio, Currency}
import com.example.portfolio.repository.PortfolioRepository
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

class PortfolioRoutes(repository: PortfolioRepository) {
  val route = pathPrefix("portfolios") {
    concat(
      pathEnd {
        post {
          entity(as[CreatePortfolioRequest]) { request =>
            val portfolio = Portfolio(
              portfolioId = java.util.UUID.randomUUID().toString,
              userId = request.userId,
              assets = List(),
              name = request.name,
              createdAt = new java.util.Date()
            )
            repository.save(portfolio)
            complete(StatusCodes.Created, portfolio)
          }
        }
      },
      path(Segment) { portfolioId =>
        concat(
          get {
            repository.findById(portfolioId) match {
              case Some(portfolio) => complete(portfolio)
              case None => complete(StatusCodes.NotFound)
            }
          },
          put {
            entity(as[UpdatePortfolioRequest]) { request =>
              repository.findById(portfolioId) match {
                case Some(portfolio) =>
                  val updated = portfolio.copy(name = request.name)
                  repository.update(updated)
                  complete(updated)
                case None => complete(StatusCodes.NotFound)
              }
            }
          },
          delete {
            repository.delete(portfolioId)
            complete(StatusCodes.NoContent)
          }
        )
      }
    )
  }
}

case class CreatePortfolioRequest(userId: String, name: String)
case class UpdatePortfolioRequest(name: String)
```

## Event Handling
The service subscribes to and publishes domain events (see `domain_events.md`) via Apache Kafka, with event sourcing in EventStoreDB for auditability. All event handlers are implemented in Java using Project Reactor for reliability.

### Subscribed Events
| **Event** | **Source** | **Action** |
|-----------|------------|------------|
| `TradeExecuted` | Transaction Management | Updates portfolio assets (add/remove based on buy/sell). Publishes `PortfolioUpdated`. |
| `DividendPaid` | Transaction Management | Updates portfolio cash or reinvests dividends. Publishes `PortfolioUpdated`. |
| `PriceUpdated` | Asset Management | Recalculates portfolio value. Publishes `PortfolioUpdated`. |
| `CustodianDataSynced` | Integration | Syncs portfolio assets with custodian data. Publishes `PortfolioUpdated`. |
| `CorporateActionApplied` | Asset Management | Adjusts asset quantities (e.g., stock split). Publishes `PortfolioUpdated`. |
| `UserCreated` | User Management | Initializes a default portfolio for the new user. Publishes `PortfolioUpdated`. |
| `ComplianceRuleUpdated` | Risk Management | Revalidates portfolio against new rules. May trigger `PortfolioRebalanced`. |

### Published Events
| **Event** | **Trigger** | **Subscribers** |
|-----------|-------------|-----------------|
| `PortfolioUpdated` | After portfolio changes (e.g., trade, price update) | Performance Calculation, Risk Management, Reporting |
| `PortfolioRebalanced` | After rebalancing to meet allocation/risk targets | Performance Calculation, Risk Management, Reporting |

### Example Event Handler
```scala
// src/portfolio-management-service/main/scala/event/TradeExecutedHandler.scala
package com.example.portfolio.event

import com.example.portfolio.domain.{Portfolio, Asset, Currency}
import com.example.portfolio.repository.PortfolioRepository
import com.example.portfolio.service.PortfolioService
import java.util.Date

case class TradeExecuted(transactionId: String, portfolioId: String, assetId: String, quantity: Double, transactionType: String, price: Double, currency: Currency, timestamp: Date)

object TradeExecutedHandler {
  def handle(event: TradeExecuted, repository: PortfolioRepository): Unit = {
    repository.findById(event.portfolioId).foreach { portfolio =>
      val updatedPortfolio = PortfolioService.updatePortfolio(portfolio, event)
      repository.update(updatedPortfolio)
      // Publish PortfolioUpdated event to Kafka
      publishPortfolioUpdated(updatedPortfolio)
    }
  }

  private def publishPortfolioUpdated(portfolio: Portfolio): Unit = {
    // Implementation using Spring Cloud Stream Kafka Producer
  }
}
```

## Persistence
- **Database**: PostgreSQL, accessed via Slick for type-safe queries.
- **Schema**:
  - Table: `portfolios`
    - Columns: `portfolio_id (PK)`, `user_id`, `name`, `created_at`
  - Table: `portfolio_assets`
    - Columns: `portfolio_id (FK)`, `asset_id`, `quantity`, `currency`
- **Repository**:
  ```scala
  // src/portfolio-management-service/main/scala/repository/PortfolioRepository.scala
  package com.example.portfolio.repository

  import com.example.portfolio.domain.{Portfolio, Asset, Currency}
  import slick.jdbc.PostgresProfile.api._
  import scala.concurrent.Future

  class PortfolioTable(tag: Tag) extends Table[(String, String, String, java.sql.Timestamp)](tag, "portfolios") {
    def portfolioId = column[String]("portfolio_id", O.PrimaryKey)
    def userId = column[String]("user_id")
    def name = column[String]("name")
    def createdAt = column[java.sql.Timestamp]("created_at")
    def * = (portfolioId, userId, name, createdAt)
  }

  class PortfolioRepository(db: Database) {
    private val portfolios = TableQuery[PortfolioTable]

    def findById(portfolioId: String): Future[Option[Portfolio]] = {
      // Implementation to fetch portfolio and assets
      db.run(portfolios.filter(_.portfolioId === portfolioId).result.headOption).map {
        case Some((id, userId, name, createdAt)) =>
          // Fetch assets from portfolio_assets table
          Some(Portfolio(id, userId, List(), name, createdAt))
        case None => None
      }
    }

    def save(portfolio: Portfolio): Future[Unit] = {
      db.run(portfolios += (portfolio.portfolioId, portfolio.userId, portfolio.name, new java.sql.Timestamp(portfolio.createdAt.getTime)))
    }

    def update(portfolio: Portfolio): Future[Unit] = {
      db.run(portfolios.filter(_.portfolioId === portfolio.portfolioId)
        .update((portfolio.portfolioId, portfolio.userId, portfolio.name, new java.sql.Timestamp(portfolio.createdAt.getTime))))
    }

    def delete(portfolioId: String): Future[Unit] = {
      db.run(portfolios.filter(_.portfolioId === portfolioId).delete).map(_ => ())
    }
  }
  ```

## Integration Points
- **Asset Management Service**: Queries `/assets/{id}/price` for real-time prices to calculate portfolio value.
- **Transaction Management Service**: Subscribes to `TradeExecuted` and `DividendPaid` events via Kafka.
- **Integration Service**: Subscribes to `CustodianDataSynced` for external holding updates and `OrderPlaced` for trade initiation.
- **User Management Service**: Queries `/users/{id}` for user validation and subscribes to `UserCreated` for portfolio initialization.
- **Kafka**: Uses Spring Cloud Stream Kafka for event publishing/subscribing (topics: `trade-executed`, `portfolio-updated`, etc.).
- **EventStoreDB**: Stores events for auditability and state reconstruction.

## Non-Functional Requirements
| **Requirement** | **How Addressed** |
|-----------------|-------------------|
| **Scalability** | Spring WebFlux and Kafka scale horizontally in Kubernetes. Maven build supports independent microservice scaling. |
| **Reliability** | Event sourcing with EventStoreDB ensures auditability. Idempotent event handlers prevent duplicate processing. |
| **Performance** | Project Reactor for low-latency event processing. Spring Data optimizes database queries. |
| **Security** | Keycloak integration for authentication/authorization. TLS for API and Kafka communication. |
| **Maintainability** | FP principles (immutable case classes) and DDD structure (domain, repository, service) ensure clean code. Tests in `src/portfolio-management-service/test/`. |
| **Compliance** | Event sourcing and compliance rule validation (via `ComplianceRuleUpdated`) support GDPR, MiFID II, and GIPS. |

## Example Workflow
1. A user creates a portfolio via `POST /portfolios`.
2. A trade is executed, triggering `TradeExecuted` from Transaction Management.
3. The service updates the portfolio using `TradeExecutedHandler` and publishes `PortfolioUpdated`.
4. Performance Calculation and Risk Management subscribe to `PortfolioUpdated`, recalculating metrics.
5. Reporting Service generates a dashboard update via `ReportGenerated`.

## Implementation Guidelines
- **Code Location**: Store code in `src/portfolio-management-service/main/scala/` with subpackages (`domain/`, `repository/`, `service/`, `api/`, `event/`).
- **Testing**: Write unit tests (e.g., `PortfolioServiceTest.java`) in `src/test/java/` using JUnit 5 and Mockito.
- **Dependencies**: Managed in `pom.xml` (Spring Boot, Spring Data, Kafka, etc.).
- **Deployment**: Package as a JAR using `mvn package` and deploy via Docker/Kubernetes (`docker/portfolio-management-service/`).
- **Consistency**: Use terms from `ubiquitous_language.md` (e.g., Portfolio, Asset) in code and documentation.

## References
- [Domain-Driven Design: Tackling Complexity in the Heart of Software](https://www.domainlanguage.com/ddd/)
- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [Spring Data Documentation](https://spring.io/projects/spring-data)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [EventStoreDB Documentation](https://www.eventstore.com/docs/)