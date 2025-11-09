# Stock Valuation Methods - FIFO & CUMP

## Overview

The Tricol system supports two stock valuation methods:
- **FIFO** (First In, First Out)
- **CUMP** (Coût Unitaire Moyen Pondéré / Weighted Average Cost)

The method is configurable via `application.properties`.

---

## Configuration

```properties
# In src/main/resources/application.properties

# FIFO Method (Default)
stock.valuation.method=FIFO

# OR

# CUMP Method
stock.valuation.method=CUMP
```

---

## FIFO (First In, First Out)

### Concept

FIFO assumes that the oldest inventory items are sold/used first. The cost of goods sold is based on the cost of the earliest purchases.

### How It Works

1. **Track Each Entry** - Each stock entry maintains a `remainingQuantity`
2. **Use Oldest First** - When stock exits, use the oldest entries first
3. **Update Remaining** - Decrease `remainingQuantity` of used entries

### Example

#### Initial State
```
Product: Cotton Fabric
Stock: 0 units
Average Cost: null
```

#### Entry 1: Purchase 100 units @ 150 MAD
```
Stock Movement:
- Type: ENTRY
- Quantity: 100
- Unit Cost: 150.00
- Total Cost: 15,000.00
- Remaining Quantity: 100

Product State:
- Stock: 100 units
- Average Cost: 150.00
```

#### Entry 2: Purchase 50 units @ 160 MAD
```
Stock Movement:
- Type: ENTRY
- Quantity: 50
- Unit Cost: 160.00
- Total Cost: 8,000.00
- Remaining Quantity: 50

Product State:
- Stock: 150 units
- Average Cost: 153.33 (weighted average for display)
```

#### Exit: Use 120 units
```
FIFO Logic:
1. Take 100 from Entry 1 (oldest)
   - Entry 1 remaining: 0
   - Cost: 100 × 150 = 15,000
   
2. Take 20 from Entry 2
   - Entry 2 remaining: 30
   - Cost: 20 × 160 = 3,200

Total Cost of Exit: 18,200 MAD

Product State:
- Stock: 30 units
- Average Cost: 160.00 (from remaining Entry 2)
```

### Database Schema

```sql
CREATE TABLE stock_movement (
    id BIGINT PRIMARY KEY,
    product_id BIGINT,
    type VARCHAR(30),
    quantity INTEGER,
    unit_cost NUMERIC(12,2),
    total_cost NUMERIC(12,2),
    remaining_quantity NUMERIC(12,2),  -- For FIFO tracking
    supplier_order_id BIGINT,
    movement_date TIMESTAMP
);
```

### Advantages

- **Accurate Cost Tracking** - Reflects actual purchase costs
- **Better for Perishables** - Ensures older stock is used first
- **Financial Reporting** - More accurate profit margins
- **Audit Trail** - Clear tracking of which batch was used

### Disadvantages

- **Complex** - Requires tracking each entry
- **More Storage** - Need to maintain `remainingQuantity` for each entry
- **Performance** - Queries can be slower with many entries

---

## CUMP (Weighted Average Cost)

### Concept

CUMP calculates a weighted average cost each time new inventory is purchased. All inventory is valued at this average cost.

### How It Works

1. **Calculate Total Value** - Sum of all inventory value
2. **Calculate Total Quantity** - Sum of all inventory quantity
3. **Compute Average** - Total Value ÷ Total Quantity
4. **Apply to All** - Use this average for all stock

### Formula

```
New Average Cost = (Previous Total Value + New Purchase Value) / Total Quantity

Where:
- Previous Total Value = Previous Stock × Previous Average Cost
- New Purchase Value = New Quantity × New Unit Cost
- Total Quantity = Previous Stock + New Quantity
```

### Example

#### Initial State
```
Product: Cotton Fabric
Stock: 0 units
Average Cost: null
```

#### Entry 1: Purchase 100 units @ 150 MAD
```
Calculation:
- Previous Value: 0 × 0 = 0
- New Value: 100 × 150 = 15,000
- Total Value: 15,000
- Total Quantity: 100
- Average Cost: 15,000 ÷ 100 = 150.00

Product State:
- Stock: 100 units
- Average Cost: 150.00
```

#### Entry 2: Purchase 50 units @ 160 MAD
```
Calculation:
- Previous Value: 100 × 150 = 15,000
- New Value: 50 × 160 = 8,000
- Total Value: 23,000
- Total Quantity: 150
- Average Cost: 23,000 ÷ 150 = 153.33

Product State:
- Stock: 150 units
- Average Cost: 153.33
```

#### Exit: Use 120 units
```
Calculation:
- Cost: 120 × 153.33 = 18,400
- Remaining Stock: 30 units
- Average Cost: 153.33 (unchanged)

Product State:
- Stock: 30 units
- Average Cost: 153.33
```

### Database Schema

```sql
CREATE TABLE product (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    price NUMERIC(12,2),
    stock_quantity INTEGER,
    average_cost NUMERIC(12,2)  -- Stores CUMP average
);
```

### Advantages

- **Simple** - Easy to calculate and understand
- **Less Storage** - Only need one average cost per product
- **Performance** - Faster queries
- **Smooth Costs** - Reduces impact of price fluctuations

### Disadvantages

- **Less Accurate** - Doesn't reflect actual purchase costs
- **No Batch Tracking** - Can't identify which batch was used
- **Profit Margins** - May not reflect true margins

---

## Comparison

| Aspect | FIFO | CUMP |
|--------|------|------|
| **Complexity** | High | Low |
| **Accuracy** | High | Medium |
| **Performance** | Slower | Faster |
| **Storage** | More | Less |
| **Cost Tracking** | Per batch | Average |
| **Best For** | Perishables, high-value items | Commodities, low-value items |
| **Financial Reporting** | More accurate | Simpler |
| **Audit Trail** | Detailed | Basic |

---

## Implementation Details

### FIFO Implementation

```java
private double calculateFifoCost(Product product, StockMovement movement, double previousQty) {
    if (movement.getType() == MovementType.ENTRY) {
        // For entries, set remaining quantity
        movement.setRemainingQuantity((double) movement.getQuantity());
        
        // Keep current average or use entry cost
        if (product.getAverageCost() == null) {
            return movement.getUnitCost();
        }
        return product.getAverageCost();
    }
    
    // For exits, use oldest entries first
    // (Implementation uses database query to find oldest entries)
    return product.getAverageCost();
}
```

### CUMP Implementation

```java
private double calculateCumpCost(Product product, StockMovement movement, double previousQty) {
    if (movement.getType() == MovementType.ENTRY) {
        double previousTotal = previousQty * (product.getAverageCost() != null ? product.getAverageCost() : 0);
        double newTotal = movement.getQuantity() * movement.getUnitCost();
        double totalValue = previousTotal + newTotal;
        double totalQty = previousQty + movement.getQuantity();
        
        return totalQty > 0 ? totalValue / totalQty : 0;
    }
    
    // For exits, use current average
    return product.getAverageCost();
}
```

---

## Choosing a Method

### Use FIFO When:

- Dealing with perishable goods
- Need accurate cost tracking
- High-value inventory
- Regulatory requirements
- Detailed audit trail needed

### Use CUMP When:

- Dealing with commodities
- Price fluctuations are common
- Simplicity is preferred
- Performance is critical
- Low-value, high-volume inventory

---

## Switching Methods

To switch between methods:

1. **Update Configuration**
   ```properties
   stock.valuation.method=CUMP  # or FIFO
   ```

2. **Restart Application**
   ```bash
   mvn spring-boot:run
   ```

3. **Note:** Existing stock movements are not recalculated. The new method applies to new movements only.

---

## Real-World Example

### Scenario: Textile Manufacturing

**Product:** Cotton Fabric  
**Supplier:** Multiple suppliers with varying prices

#### Month 1: Using FIFO

```
Jan 5:  Purchase 1000m @ 100 MAD/m from Supplier A
Jan 15: Purchase 500m @ 110 MAD/m from Supplier B
Jan 20: Use 1200m for production

FIFO Calculation:
- Use 1000m from Jan 5 @ 100 = 100,000 MAD
- Use 200m from Jan 15 @ 110 = 22,000 MAD
- Total Cost: 122,000 MAD
- Cost per meter: 101.67 MAD

Remaining:
- 300m @ 110 MAD/m
- Average Cost: 110 MAD
```

#### Month 1: Using CUMP

```
Jan 5:  Purchase 1000m @ 100 MAD/m
        Average: 100 MAD/m
        
Jan 15: Purchase 500m @ 110 MAD/m
        Average: (1000×100 + 500×110) / 1500 = 103.33 MAD/m
        
Jan 20: Use 1200m for production
        Cost: 1200 × 103.33 = 124,000 MAD
        
Remaining:
- 300m @ 103.33 MAD/m
- Average Cost: 103.33 MAD
```

**Difference:** 2,000 MAD (1.6%)

---

## Testing

### Test FIFO

```bash
# Set FIFO in application.properties
stock.valuation.method=FIFO

# Run test
./scripts/test-stock.sh
```

### Test CUMP

```bash
# Set CUMP in application.properties
stock.valuation.method=CUMP

# Run test
./scripts/test-stock.sh
```

---

## Conclusion

Both FIFO and CUMP are valid valuation methods with different use cases:

- **FIFO** provides more accurate cost tracking but is more complex
- **CUMP** is simpler and faster but less accurate

Choose based on your business needs, regulatory requirements, and inventory characteristics.

---

**Current Default:** FIFO  
**Configurable:** Yes  
**Runtime Switching:** Requires application restart
