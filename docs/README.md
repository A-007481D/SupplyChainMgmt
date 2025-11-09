# Documentation Index

Welcome to the Tricol Supplier Order Management System documentation.

---

## Available Documentation

### 1. **[API Examples](API_EXAMPLES.md)** 
Complete API reference with request/response examples for all endpoints.

**Contents:**
- Supplier API endpoints
- Product API endpoints
- Order API endpoints
- Stock API endpoints
- Complete workflow examples
- Error responses
- Pagination guide

**Best for:** Developers integrating with the API

---

### 2. **[Architecture Guide](ARCHITECTURE.md)** 
Detailed explanation of the system architecture.

**Contents:**
- Hexagonal Architecture pattern
- Layer structure (API, Application, Domain, Infrastructure)
- Module organization
- Design principles
- Data flow diagrams
- Technology stack

**Best for:** Developers understanding the codebase structure

---

### 3. **[Stock Valuation](STOCK_VALUATION.md)**
In-depth explanation of FIFO and CUMP valuation methods.

**Contents:**
- FIFO (First In, First Out) explained
- CUMP (Weighted Average Cost) explained
- Comparison and use cases
- Implementation details
- Real-world examples
- Configuration guide

**Best for:** Business analysts and developers working with inventory

---

## Quick Start Guides

### For Developers

1. Read the [Main README](../README.md) for setup instructions
2. Review [Architecture Guide](ARCHITECTURE.md) to understand the structure
3. Check [API Examples](API_EXAMPLES.md) for endpoint usage
4. Run test scripts in `/scripts/` directory

### For Business Users

1. Read [Stock Valuation](STOCK_VALUATION.md) to understand inventory methods
2. Review [API Examples](API_EXAMPLES.md) for workflow understanding
3. Access Swagger UI at http://localhost:8080/swagger-ui/index.html

### For Testers

1. Run automated tests: `./scripts/test-all.sh`
2. Run workflow test: `./scripts/test-workflow.sh`
3. Check individual modules: `./scripts/test-suppliers.sh`, etc.
4. Review [API Examples](API_EXAMPLES.md) for manual testing

---

## Diagrams

Visual architecture diagrams are available in the `/diagrams/` directory:

- **domain-class-diagram.mmd** - Entity relationships
- **sequence-order-delivery.mmd** - Order delivery workflow
- **architecture-layers.mmd** - System layers
- **hexagonal-architecture.mmd** - Ports and adapters
- **database-erd.mmd** - Database schema

**View with:** VS Code + Mermaid extension, or https://mermaid.live/

---

## Testing

### Automated Test Scripts

Located in `/scripts/` directory:

- `test-all.sh` - Run all tests
- `test-workflow.sh` - Complete order-to-stock workflow
- `test-suppliers.sh` - Supplier CRUD tests
- `test-products.sh` - Product CRUD tests
- `test-orders.sh` - Order CRUD and lifecycle tests
- `test-stock.sh` - Stock movement tests

### Running Tests

```bash
# Make scripts executable
chmod +x scripts/*.sh

# Run all tests
./scripts/test-all.sh

# Run specific module test
./scripts/test-suppliers.sh
```

---

## Configuration

### Stock Valuation Method

Edit `src/main/resources/application.properties`:

```properties
# FIFO (First In, First Out) - Default
stock.valuation.method=FIFO

# OR

# CUMP (Weighted Average Cost)
stock.valuation.method=CUMP
```

### Database

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tricolv2_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

---

## Additional Resources

### API Documentation

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **OpenAPI Spec:** http://localhost:8080/v3/api-docs

### Project Files

- **Main README:** [../README.md](../README.md)
- **Test Scripts:** [../scripts/](../scripts/)
- **Diagrams:** [../diagrams/](../diagrams/)
- **Source Code:** [../src/](../src/)

---

## Key Features

### Supplier Management
- Complete CRUD operations
- Pagination and filtering
- Validation

### Product Management
- Full catalog management
- Stock tracking
- Average cost calculation
- Search and filtering

### Order Management
- Multi-item orders
- Status workflow (WAITING → VALIDATED → DELIVERED)
- Automatic total calculation
- Status validation

### Stock Management
- **Automatic stock movements** on order delivery
- Manual movements (ENTRY, EXIT, ADJUSTMENT)
- Movement history
- FIFO and CUMP valuation

---

##  Architecture Highlights

- **Hexagonal Architecture** - Clean, maintainable structure
- **Domain-Driven Design** - Business logic at the core
- **Ports & Adapters** - Flexible and testable
- **RESTful API** - Standard HTTP endpoints
- **Swagger Documentation** - Interactive API docs

---

## Technology Stack

- Spring Boot 3.5.7
- Spring Data JPA
- MapStruct 1.6.3
- Liquibase
- Swagger/OpenAPI 2.8.8
- PostgreSQL
- Maven

---

## Support

For questions or issues:

1. Check this documentation
2. Review test scripts for examples
3. Access Swagger UI for interactive API testing
4. Open an issue on GitHub

---

##  Documentation Roadmap

### Current Documentation 
- API Examples
- Architecture Guide
- Stock Valuation Guide
- Test Scripts
- Diagrams

### Planned Documentation 
- Deployment Guide
- Performance Tuning
- Security Best Practices
- Monitoring and Logging
- Troubleshooting Guide
- FAQ

---

##  Document Versions

| Document | Version | Last Updated |
|----------|---------|--------------|
| API Examples | 1.0 | Nov 9, 2025 |
| Architecture | 1.0 | Nov 9, 2025 |
| Stock Valuation | 1.0 | Nov 9, 2025 |
| Main README | 1.0 | Nov 9, 2025 |

---

**Made with ❤️ for Tricol Professional Clothing Manufacturing**

**Status:** Complete and Up-to-Date  
