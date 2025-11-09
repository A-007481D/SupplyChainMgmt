# Tricol Architecture Diagrams

This directory contains all the Mermaid diagrams for the Tricol Supplier Order Management System.

---

## 📁 Diagram Files

### 1. **domain-class-diagram.mmd** 
**Focus:** Domain entities and their relationships

**Shows:**
- Core business entities (Supplier, Product, SupplierOrder, StockMovement)
- Entity attributes and key methods
- Relationships between entities
- Enumerations (OrderStatus, MovementType, Category)
- Business rules and notes

**Best for:** Understanding the data model and business relationships

---

### 2. **sequence-order-delivery.mmd** 
**Focus:** Complete order delivery workflow

**Shows:**
- Step-by-step process from order creation to delivery
- Automatic stock movement creation
- Database interactions
- FIFO/CUMP cost calculation
- Component interactions

**Best for:** Understanding the order-to-stock workflow

---

### 3. **architecture-layers.mmd** 
**Focus:** Layered architecture overview

**Shows:**
- API Layer (Controllers)
- Application Layer (Services)
- Domain Layer (Models & Ports)
- Infrastructure Layer (Adapters & Repositories)
- Database Layer

**Best for:** High-level architecture understanding

---

### 4. **hexagonal-architecture.mmd** ⬡
**Focus:** Hexagonal/Ports & Adapters architecture

**Shows:**
- Application core (hexagon)
- Input ports (service interfaces)
- Output ports (repository interfaces)
- Input adapters (REST controllers)
- Output adapters (repository implementations)
- Dependency directions

**Best for:** Understanding the hexagonal architecture pattern

---

### 5. **database-erd.mmd** 🗄️
**Focus:** Database entity-relationship diagram

**Shows:**
- Database tables
- Columns and data types
- Primary keys (PK)
- Foreign keys (FK)
- Relationships and cardinality

**Best for:** Understanding the database schema

---

## How to View

### **Option 1: Intellij IDEA or VSCode (Recommended)**
1. Install extension: **Markdown Preview Mermaid Support**
2. Open any `.mmd` file
3. Right-click → **Open Preview** (or press `Ctrl+Shift+V`)
4. Diagrams render beautifully!

### **Option 2: Mermaid Live Editor**
1. Go to https://mermaid.live/
2. Copy the content of any `.mmd` file
3. Paste into the editor
4. View and export as PNG/SVG

### **Option 3: GitHub**
- Push to GitHub - diagrams render automatically in markdown files

### **Option 4: IntelliJ IDEA**
1. Install plugin: **Mermaid**
2. Open `.mmd` files
3. View rendered diagram in preview pane

---

## Diagram Descriptions

### **Domain Class Diagram**
```
Supplier ──► Product (supplies)
Supplier ──► SupplierOrder (places)
SupplierOrder ◄──► SupplierOrderItem (contains)
SupplierOrderItem ──► Product (references)
SupplierOrder ──► StockMovement (generates)
Product ──► StockMovement (tracks)
```

### **Order Delivery Sequence**
```
User → Controller → OrderService → StockAdapter → StockService
                                                      ↓
                                            ProductRepo ← → Database
                                                      ↓
                                             StockRepo ← → Database
```

### **Architecture Layers**
```
API Layer (Controllers)
    ↓
Application Layer (Services)
    ↓
Domain Layer (Models & Ports)
    ↓
Infrastructure Layer (Adapters)
    ↓
Database (PostgreSQL)
```

---

## Key Concepts Illustrated

### **1. Hexagonal Architecture**
- **Core Domain** is independent of external concerns
- **Ports** define interfaces
- **Adapters** implement interfaces
- **Dependencies** point inward (toward domain)

### **2. Order-to-Stock Flow**
1. Order created (status = WAITING)
2. Order validated (status = VALIDATED)
3. Order delivered (status = DELIVERED)
4. **Stock movements created automatically**
5. Product stock updated
6. Average cost calculated (FIFO/CUMP)

### **3. Stock Valuation**
- **FIFO:** First In, First Out (tracks remaining quantity)
- **CUMP:** Weighted Average Cost (recalculates on each entry)

---

## Notes

- All diagrams are based on the **actual implementation**
- Class names, methods, and relationships match the codebase
- Diagrams are **accurate** and **up-to-date**
- Use these for documentation, presentations, or onboarding

---

## Updates

**Version:** 1.0  
**Status:** ✅ Complete and Accurate

---
**Architecture:** Hexagonal/DDD  
**Technologies:** Spring Boot, JPA, PostgreSQL
