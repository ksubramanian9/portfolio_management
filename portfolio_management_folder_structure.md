# Folder Structure

The folder structure of the Investment Portfolio Manager application’s GitHub repository is designed to organize code, documentation, and supporting assets for a microservices-based architecture built with **Java** and **Spring Boot**, adhering to Domain-Driven Design (DDD) and Event-Driven Architecture (EDA) principles. The structure separates source code (`src/`), documentation (`docs/`), assets (`assets/`), and deployment configurations (`docker/`), ensuring modularity, maintainability, and alignment with non-functional requirements like scalability, reliability, and security. This document describes the repository’s organization, including where code for each microservice is stored, and aligns with the ubiquitous language, bounded contexts, and domain events defined in related documentation.

## Repository Structure

```
investment-portfolio-manager/
├── docs/
│   ├── architecture/
│   │   ├── domain_driven_design/
│   │   │   ├── bounded_contexts.md
│   │   │   ├── domain_events.md
│   │   │   ├── ubiquitous_language.md
│   │   │   └── folder_structure.md
│   │   ├── microservices/
│   │   │   ├── portfolio_management_service.md
│   │   │   ├── asset_management_service.md
│   │   │   ├── transaction_management_service.md
│   │   │   ├── performance_calculation_service.md
│   │   │   ├── risk_management_service.md
│   │   │   ├── reporting_service.md
│   │   │   ├── user_management_service.md
│   │   │   ├── integration_service.md
│   │   │   └── ...
│   │   └── system_design/
│   │       ├── overview.md
│   │       ├── technology_stack.md
│   │       └── ...
│   ├── api/
│   │   ├── portfolio_management_api.md
│   │   ├── asset_management_api.md
│   │   └── ...
│   └── guides/
│       ├── setup.md
│       ├── development.md
│       ├── deployment.md
│       └── ...
├── src/
│   ├── portfolio-management-service/
│   │   ├── main/
│   │   │   ├── scala/
│   │   │   │   ├── domain/
│   │   │   │   │   ├── Portfolio.scala
│   │   │   │   │   ├── Asset.scala
│   │   │   │   │   ├── Currency.scala
│   │   │   │   │   └── ...
│   │   │   │   ├── repository/
│   │   │   │   │   ├── PortfolioRepository.scala
│   │   │   │   │   └── ...
│   │   │   │   ├── service/
│   │   │   │   │   ├── PortfolioService.java
│   │   │   │   │   ├── PortfolioPerformanceCalculator.java
│   │   │   │   │   └── ...
│   │   │   │   ├── api/
│   │   │   │   │   ├── PortfolioController.java
│   │   │   │   │   └── ...
│   │   │   │   └── event/
│   │   │   │       ├── TradeExecutedHandler.java
│   │   │   │       ├── PortfolioUpdatedHandler.scala
│   │   │   │       └── ...
│   │   └── test/
│   │       ├── scala/
│   │       │   ├── domain/
│   │       │   │   ├── PortfolioTest.java
│   │       │   │   └── ...
│   │       │   ├── repository/
│   │       │   │   ├── PortfolioRepositorySpec.scala
│   │       │   │   └── ...
│   │       │   ├── service/
│   │       │   └── ...
│   ├── asset-management-service/
│   │   ├── main/
│   │   │   ├── scala/
│   │   │   │   ├── domain/
│   │   │   │   │   ├── Asset.scala
│   │   │   │   │   ├── AssetType.scala
│   │   │   │   │   └── ...
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── api/
│   │   │   │   └── event/
│   │   │   │       ├── PriceUpdatedHandler.scala
│   │   │   │       └── ...
│   │   └── test/
│   │       ├── scala/
│   │       │   ├── domain/
│   │       │   ├── repository/
│   │       │   ├── service/
│   │       │   └── ...
│   ├── transaction-management-service/
│   │   ├── main/
│   │   │   ├── scala/
│   │   │   │   ├── domain/
│   │   │   │   │   ├── Transaction.scala
│   │   │   │   │   └── ...
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── api/
│   │   │   │   └── event/
│   │   │   │       ├── TradeExecutedHandler.scala
│   │   │   │       └── ...
│   │   └── test/
│   │       ├── scala/
│   ├── performance-calculation-service/
│   │   ├── main/
│   │   │   ├── scala/
│   │   │   │   ├── domain/
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── api/
│   │   │   │   └── event/
│   │   │   │       ├── PerformanceCalculatedHandler.scala
│   │   │   │       └── ...
│   │   └── test/
│   │       ├── scala/
│   ├── risk-management-service/
│   │   ├── main/
│   │   │   ├── scala/
│   │   │   │   ├── domain/
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── api/
│   │   │   │   └── event/
│   │   │   │       ├── RiskAssessmentUpdatedHandler.scala
│   │   │   │       └── ...
│   │   └── test/
│   │       ├── scala/
│   ├── reporting-service/
│   │   ├── main/
│   │   │   ├── scala/
│   │   │   │   ├── domain/
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── api/
│   │   │   │   └── event/
│   │   │   │       ├── ReportGeneratedHandler.scala
│   │   │   │       └── ...
│   │   └── test/
│   │       ├── scala/
│   ├── user-management-service/
│   │   ├── main/
│   │   │   ├── scala/
│   │   │   │   ├── domain/
│   │   │   │   │   ├── User.scala
│   │   │   │   │   └── ...
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── api/
│   │   │   │   └── event/
│   │   │   │       ├── UserCreatedHandler.scala
│   │   │   │       ├── UserUpdatedHandler.scala
│   │   │   │       └── ...
│   │   └── test/
│   │       ├── scala/
│   ├── integration-service/
│   │   ├── main/
│   │   │   ├── scala/
│   │   │   │   ├── domain/
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── api/
│   │   │   │   └── event/
│   │   │   │       ├── MarketDataUpdatedHandler.scala
│   │   │   │       ├── CustodianDataSyncedHandler.scala
│   │   │   │       └── ...
│   │   └── test/
│   │       ├── scala/
│   └── ...
├── assets/
│   ├── diagrams/
│   │   ├── domain_model_diagram.png
│   │   ├── architecture_diagram.png
│   │   └── ...
│   └── data/
│       ├── sample_portfolios.json
│       └── ...
├── docker/
│   ├── portfolio-management-service/
│   │   ├── Dockerfile
│   │   └── kubernetes/
│   │       ├── deployment.yaml
│   │       └── ...
│   ├── asset-management-service/
│   │   ├── Dockerfile
│   │   └── kubernetes/
│   └── ...
├── pom.xml
├── README.md
├── LICENSE
└── .gitignore
```

## Directory Descriptions

### `docs/`
- **Purpose**: Contains all documentation for the project, including architecture, API specifications, and development guides.
- **Subdirectories**:
  - **`architecture/domain_driven_design/`**: Stores DDD-related documents, such as `ubiquitous_language.md`, `bounded_contexts.md`, `domain_events.md`, and `folder_structure.md`, defining the domain model and event-driven interactions.
  - **`architecture/microservices/`**: Contains microservice-specific documentation (e.g., `portfolio_management_service.md`), detailing responsibilities, domain models, APIs, and events.
  - **`architecture/system_design/`**: Includes high-level system design documents (e.g., `technology_stack.md`, `overview.md`).
  - **`api/`**: Stores API specifications for each microservice (e.g., `portfolio_management_api.md`).
  - **`guides/`**: Contains guides for setup, development, and deployment (e.g., `setup.md`).
- **Rationale**: Centralizes documentation to ensure alignment with DDD principles and developer clarity.

### `src/`
- **Purpose**: Contains all source code for the application, organized by microservice to support the microservices architecture.
- **Subdirectories** (per microservice, e.g., `portfolio-management-service/`):
  - **`main/scala/`**: Production code, organized into subpackages:
    - **`domain/`**: Domain models (e.g., `Portfolio.scala`, `Asset.scala`) implementing entities, aggregates, and value objects as per DDD.
    - **`repository/`**: Repository interfaces and implementations (e.g., `PortfolioRepository.scala`) for data persistence (e.g., PostgreSQL via Slick).
    - **`service/`**: Domain and application services (e.g., `PortfolioService.scala`, `PortfolioPerformanceCalculator.scala`) for business logic.
    - **`api/`**: REST API routes and handlers (e.g., `PortfolioRoutes.scala`) using Spring WebFlux.
    - **`event/`**: Event handlers (e.g., `TradeExecutedHandler.scala`) for processing Kafka events.
  - **`test/java/`**: Unit and integration tests, mirroring the `main/scala/` structure (e.g., `PortfolioSpec.scala` using JUnit 5).
- **Rationale**: Follows standard Maven project conventions (Maven structure) and DDD principles, ensuring modularity and testability. Each microservice is self-contained for independent development and deployment.

### `assets/`
- **Purpose**: Stores static assets like diagrams and sample data.
- **Subdirectories**:
  - **`diagrams/`**: UML and architecture diagrams (e.g., `domain_model_diagram.png`) to visualize domain models and system architecture.
  - **`data/`**: Sample datasets (e.g., `sample_portfolios.json`) for testing or seeding databases.
- **Rationale**: Separates non-code assets from code and documentation for clarity.

### `docker/`
- **Purpose**: Contains Docker and Kubernetes configurations for deploying microservices.
- **Subdirectories** (per microservice, e.g., `portfolio-management-service/`):
  - **`Dockerfile`**: Defines the container image for the microservice.
  - **`kubernetes/`**: Kubernetes manifests (e.g., `deployment.yaml`) for orchestration.
- **Rationale**: Supports containerized deployment in Kubernetes, aligning with the technology stack (AWS/GCP, Kubernetes).

### Root-Level Files
- **`pom.xml`**: Defines the project structure and dependencies (Spring Boot, Spring Cloud Stream, Axon Framework) using Maven.
  ```xml
  <project>
      <!-- dependencies and plugins -->
  </project>
  ```
- **`README.md`**: Provides an overview of the project, setup instructions, and links to key documentation.
- **`LICENSE`**: Specifies the project’s licensing terms.
- **`.gitignore`**: Excludes build artifacts, logs, and temporary files from version control.

## Non-Functional Requirements Alignment
| **Requirement** | **How Addressed** |
|-----------------|-------------------|
| **Scalability** | Microservice-specific folders in `src/` enable independent scaling via Kubernetes. |
| **Reliability** | Separate `test/` directories per microservice ensure comprehensive testing with JUnit 5. |
| **Maintainability** | Organized package structure (`domain/`, `repository/`, etc.) and FP principles in Java improve code clarity. Documentation in `docs/` ensures alignment. |
| **Security** | Code in `src/*/main/java/service/` integrates with Keycloak and Vault for secure access. |
| **Performance** | Spring-based implementations in `src/*/main/java/api/` and `event/` optimize low-latency processing. |

## Guidelines
- **Code Organization**: Place all Java code in `src/<microservice>/main/java/`, organized by DDD layers (domain, repository, service, api, event). Tests go in `src/<microservice>/test/java/`.
- **Documentation**: Store DDD-related documents in `docs/architecture/domain_driven_design/`, microservice documentation in `docs/architecture/microservices/`, and API specs in `docs/api/`.
- **Consistency**: Use the ubiquitous language (see `docs/architecture/domain_driven_design/ubiquitous_language.md`) in code and documentation for consistency.
- **Extensibility**: Add new microservices as subdirectories under `src/` (e.g., `src/new-service/`) and corresponding documentation in `docs/architecture/microservices/`.
- **Version Control**: Use GitHub pull requests to track changes to code and documentation, ensuring alignment.

## References
- [Java Project Structure with Maven](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html)
- [Domain-Driven Design: Tackling Complexity in the Heart of Software](https://www.domainlanguage.com/ddd/)
- [Microservices Architecture](https://microservices.io/)