# Architecture Documentation - Tricol

## Overview

This document describes the architecture of the Tricol Supplier Order Management System, which follows **Hexagonal Architecture** (also known as Ports and Adapters) combined with **Domain-Driven Design (DDD)** principles.

---

## Table of Contents

1. [Architectural Pattern](#architectural-pattern)
2. [Layer Structure](#layer-structure)
3. [Module Organization](#module-organization)
4. [Design Principles](#design-principles)
5. [Key Components](#key-components)
6. [Data Flow](#data-flow)
7. [Technology Stack](#technology-stack)

---

## Architectural Pattern

### Hexagonal Architecture (Ports & Adapters)

The application is built using Hexagonal Architecture, which provides:

- **Independence from frameworks** - Business logic doesn't depend on Spring, JPA, or any framework
- **Testability** - Core business logic can be tested without external dependencies
- **Flexibility** - Easy to swap implementations (e.g., change database, add new API)
- **Maintainability** - Clear separation of concerns

```
        ┌─────────────────────────────────┐
        │     External Systems            │
        │  (HTTP, Database, etc.)         │
        └──────────┬──────────────────────┘
                   │
        ┌──────────▼──────────────────────┐
        │      Input Adapters             │
        │   (REST Controllers, etc.)      │
        └──────────┬──────────────────────┘
                   │
        ┌──────────▼──────────────────────┐
        │      Input Ports                │
        │    (Service Interfaces)         │
        └──────────┬──────────────────────┘
                   │
        ┌──────────▼──────────────────────┐
        │    ⬡ APPLICATION CORE ⬡        │
        │                                 │
        │  ┌────────────────────────────┐ │
        │  │   Domain Models            │ │
        │  │   Business Logic           │ │
        │  │   Domain Services          │ │
        │  └────────────────────────────┘ │
        │                                 │
        └──────────┬──────────────────────┘
                   │
        ┌──────────▼──────────────────────┐
        │      Output Ports               │
        │   (Repository Interfaces)       │
        └──────────┬──────────────────────┘
                   │
        ┌──────────▼──────────────────────┐
        │     Output Adapters             │
        │  (JPA Repositories, etc.)       │
        └──────────┬──────────────────────┘
                   │
        ┌──────────▼──────────────────────┐
        │     External Systems            │
        │      (PostgreSQL)               │
        └─────────────────────────────────┘
```

---

## Layer Structure

### 1. API Layer (Input Adapters)

**Purpose:** Handle HTTP requests and responses

**Components:**
- **Controllers** - REST endpoints
- **DTOs** - Request/Response data transfer objects
- **Mappers** - Convert between DTOs and Domain models (MapStruct)

**Example:**
```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductServicePort productService;
    private final ProductApiMapper mapper;
    
    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@RequestBody ProductRequestDTO request) {
        Product domain = mapper.toDomain(request);
        Product saved = productService.create(domain);
        return ResponseEntity.ok(mapper.toDto(saved));
    }
}
```

### 2. Application Layer (Business Logic)

**Purpose:** Implement business rules and orchestrate domain operations

**Components:**
- **Services** - Business logic implementation
- **Ports** - Service interfaces (Input Ports)

**Example:**
```java
@Service
public class ProductServiceImpl implements ProductServicePort {
    private final ProductRepositoryPort repository;
    
    @Override
    public Product create(Product product) {
        // Business logic here
        return repository.save(product);
    }
}
```

### 3. Domain Layer (Core)

**Purpose:** Define the business domain

**Components:**
- **Models** - Pure domain entities (no JPA annotations)
- **Enums** - Domain enumerations
- **Exceptions** - Domain-specific exceptions
- **Ports** - Repository interfaces (Output Ports)

**Example:**
```java
public class Product {
    private Long id;
    private String name;
    private Double price;
    private Integer stockQuantity;
    private Double averageCost;
    
    // Pure business logic methods
    public void updateStock(int quantity) {
        this.stockQuantity += quantity;
    }
}
```

### 4. Infrastructure Layer (Output Adapters)

**Purpose:** Implement technical concerns (database, external APIs)

**Components:**
- **Adapters** - Implement repository ports
- **JPA Entities** - Database entities with JPA annotations
- **JPA Repositories** - Spring Data JPA repositories
- **Mappers** - Convert between Domain and JPA entities

**Example:**
```java
@Component
public class ProductRepositoryPortAdapter implements ProductRepositoryPort {
    private final ProductJpaRepository jpaRepository;
    private final ProductJpaMapper mapper;
    
    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = mapper.toEntity(product);
        ProductJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
```

---

## Module Organization

Each business module follows the same structure:

```
module/
├── api/
│   ├── controller/
│   │   └── ModuleController.java
│   ├── dto/
│   │   ├── ModuleRequestDTO.java
│   │   └── ModuleResponseDTO.java
│   └── mapper/
│       └── ModuleApiMapper.java
│
├── application/
│   ├── service/
│   │   └── ModuleServiceImpl.java
│   └── ports/
│       └── ModuleServicePort.java
│
├── domain/
│   ├── model/
│   │   └── Module.java
│   ├── enums/
│   │   └── ModuleStatus.java
│   ├── exception/
│   │   └── ModuleException.java
│   └── ports/
│       └── ModuleRepositoryPort.java
│
└── infrastructure/
    ├── adapter/
    │   └── ModuleRepositoryPortAdapter.java
    └── persistence/
        ├── entity/
        │   └── ModuleJpaEntity.java
        ├── mapper/
        │   └── ModuleJpaMapper.java
        └── repository/
            └── ModuleJpaRepository.java
```

---

## Design Principles

### 1. Dependency Rule

Dependencies point **inward** toward the domain:

```
API → Application → Domain ← Infrastructure
```

- **Domain** has no dependencies on outer layers
- **Application** depends only on Domain
- **Infrastructure** depends on Domain (implements ports)
- **API** depends on Application (uses services)

### 2. Separation of Concerns

Each layer has a single responsibility:

- **API**: HTTP handling
- **Application**: Business logic
- **Domain**: Business rules and entities
- **Infrastructure**: Technical implementation

### 3. Interface Segregation

Ports (interfaces) are small and focused:

```java
public interface ProductServicePort {
    Product create(Product product);
    Product findById(Long id);
    Page<Product> findAll(Pageable pageable);
}
```

### 4. Dependency Inversion

High-level modules don't depend on low-level modules. Both depend on abstractions (ports):

```java
// High-level (Service)
public class ProductServiceImpl implements ProductServicePort {
    private final ProductRepositoryPort repository; // Depends on abstraction
}

// Low-level (Adapter)
public class ProductRepositoryPortAdapter implements ProductRepositoryPort {
    // Implements abstraction
}
```

---

## Key Components

### Controllers

Handle HTTP requests, validate input, and return responses.

**Responsibilities:**
- Route HTTP requests
- Validate request data (Jakarta Validation)
- Convert DTOs to domain models
- Return appropriate HTTP status codes

### Services

Implement business logic and orchestrate operations.

**Responsibilities:**
- Execute business rules
- Coordinate multiple repositories
- Handle transactions
- Throw domain exceptions

### Domain Models

Pure business entities without framework dependencies.

**Characteristics:**
- No JPA annotations
- Business logic methods
- Immutable when possible
- Rich domain model (not anemic)

### Repositories

Abstract data access.

**Responsibilities:**
- Save and retrieve entities
- Query data
- No business logic

### Adapters

Bridge between domain and infrastructure.

**Types:**
- **Input Adapters**: Controllers
- **Output Adapters**: Repository implementations
- **Cross-Module Adapters**: StockAdapter (bridges Order and Stock modules)

---

## Data Flow

### Request Flow (Create Product)

```
1. HTTP POST /api/v1/products
   ↓
2. ProductController receives ProductRequestDTO
   ↓
3. ProductApiMapper converts DTO → Product (domain)
   ↓
4. ProductServiceImpl.create(product)
   ↓
5. Business logic validation
   ↓
6. ProductRepositoryPort.save(product)
   ↓
7. ProductRepositoryPortAdapter.save(product)
   ↓
8. ProductJpaMapper converts Product → ProductJpaEntity
   ↓
9. ProductJpaRepository.save(entity)
   ↓
10. PostgreSQL INSERT
   ↓
11. Return saved entity
   ↓
12. Convert back: JpaEntity → Product → DTO
   ↓
13. HTTP 201 Created with ProductResponseDTO
```

### Order Delivery Flow (Complex)

```
1. HTTP PUT /api/v1/orders/{id}/status {status: DELIVERED}
   ↓
2. SupplierOrderController
   ↓
3. SupplierOrderService.updateStatus(id, DELIVERED)
   ↓
4. Validate status transition
   ↓
5. Update order status
   ↓
6. updateStockForOrder(order)
   ↓
7. For each order item:
   ├─ StockAdapter.recordEntry(productId, quantity, orderId, price)
   │  ↓
   ├─ StockServiceImpl.recordMovement(...)
   │  ↓
   ├─ Create StockMovement
   │  ↓
   ├─ Update Product stock quantity
   │  ↓
   ├─ Calculate average cost (FIFO/CUMP)
   │  ↓
   └─ Save movement and product
   ↓
8. Return updated order
```

---

## Technology Stack

### Core Framework
- **Spring Boot 3.5.7** - Application framework
- **Spring Data JPA** - Data access
- **Spring Web** - REST API

### Mapping
- **MapStruct 1.6.3** - DTO ↔ Domain ↔ Entity mapping
- **Lombok 1.18.32** - Reduce boilerplate

### Database
- **PostgreSQL** - Relational database
- **Liquibase** - Database migrations

### Documentation
- **Swagger/OpenAPI 2.8.8** - API documentation

### Build
- **Maven** - Dependency management

---

## Benefits of This Architecture

### 1. Testability

Test business logic without Spring or database:

```java
@Test
void testCreateProduct() {
    ProductRepositoryPort mockRepo = mock(ProductRepositoryPort.class);
    ProductServiceImpl service = new ProductServiceImpl(mockRepo);
    
    Product product = new Product(...);
    service.create(product);
    
    verify(mockRepo).save(product);
}
```

### 2. Flexibility

Easy to change implementations:

- Swap PostgreSQL for MongoDB
- Add GraphQL API alongside REST
- Change from JPA to JDBC

### 3. Maintainability

Clear boundaries make changes easier:

- Change API without touching business logic
- Update business rules without changing database
- Add new features in isolated modules

### 4. Scalability

Modules can be extracted into microservices:

- Each module is already independent
- Clear interfaces between modules
- No tight coupling

---

## Module Dependencies

```
┌─────────────┐
│   Supplier  │
└──────┬──────┘
       │
       ▼
┌─────────────┐     ┌─────────────┐
│   Product   │────▶│    Stock    │
└──────┬──────┘     └─────────────┘
       │                    ▲
       ▼                    │
┌─────────────┐             │
│    Order    │─────────────┘
└─────────────┘
```

**Dependencies:**
- **Order** depends on Supplier, Product, and Stock (via ports)
- **Product** depends on Supplier
- **Stock** depends on Product
- **Supplier** has no dependencies

---

## Design Patterns Used

1. **Hexagonal Architecture** - Overall structure
2. **Repository Pattern** - Data access abstraction
3. **Adapter Pattern** - StockAdapter, Repository adapters
4. **Strategy Pattern** - FIFO vs CUMP valuation
5. **DTO Pattern** - Separate API models from domain
6. **Mapper Pattern** - Convert between layers
7. **Service Layer Pattern** - Business logic encapsulation

---

## Future Enhancements

1. **Event-Driven Architecture** - Domain events for stock movements
2. **CQRS** - Separate read and write models
3. **Microservices** - Extract modules into separate services
4. **API Gateway** - Centralized API management
5. **Message Queue** - Async processing for stock movements

---

**For visual diagrams, see `/diagrams/` directory.**
