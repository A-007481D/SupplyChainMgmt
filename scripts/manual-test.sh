#!/bin/bash

echo "=========================================="
echo "MANUAL WORKFLOW TEST"
echo "=========================================="
echo ""

# Step 1: Create Supplier
echo "Step 1: Creating Supplier..."
SUPPLIER=$(curl -s -X POST "http://localhost:8080/api/v1/suppliers" \
  -H "Content-Type: application/json" \
  -d '{
    "company": "Test Supplier Ltd",
    "address": "123 Test Street",
    "contact": "John Doe",
    "email": "test@supplier.com",
    "phone": "+212123456789",
    "city": "Casablanca",
    "ice": "001122334455667"
  }')

echo "$SUPPLIER" | jq '.'
SUPPLIER_ID=$(echo "$SUPPLIER" | jq -r '.id')
echo "✓ Supplier ID: $SUPPLIER_ID"
echo ""

# Step 2: Create Product
echo "Step 2: Creating Product..."
PRODUCT=$(curl -s -X POST "http://localhost:8080/api/v1/products" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Fabric",
    "description": "High quality fabric",
    "price": 100.00,
    "category": "TEXTILE",
    "unit": "metre",
    "stockQuantity": 0,
    "supplierId": '$SUPPLIER_ID'
  }')

echo "$PRODUCT" | jq '.'
PRODUCT_ID=$(echo "$PRODUCT" | jq -r '.id')
echo "✓ Product ID: $PRODUCT_ID"
echo "✓ Initial Stock: $(echo "$PRODUCT" | jq -r '.stockQuantity')"
echo ""

# Step 3: Create Order
echo "Step 3: Creating Order..."
ORDER=$(curl -s -X POST "http://localhost:8080/api/v1/orders" \
  -H "Content-Type: application/json" \
  -d '{
    "supplierId": '$SUPPLIER_ID',
    "items": [
      {
        "productId": '$PRODUCT_ID',
        "quantity": 50,
        "unitPrice": 100.00
      }
    ]
  }')

echo "$ORDER" | jq '.'
ORDER_ID=$(echo "$ORDER" | jq -r '.id')
ORDER_STATUS=$(echo "$ORDER" | jq -r '.status')
ORDER_TOTAL=$(echo "$ORDER" | jq -r '.totalAmount')
echo "✓ Order ID: $ORDER_ID"
echo "✓ Status: $ORDER_STATUS"
echo "✓ Total: $ORDER_TOTAL"
echo ""

# Step 4: Validate Order
echo "Step 4: Validating Order..."
VALIDATED=$(curl -s -X PUT "http://localhost:8080/api/v1/orders/$ORDER_ID/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "VALIDATED"}')

echo "$VALIDATED" | jq '.'
echo "✓ Status: $(echo "$VALIDATED" | jq -r '.status')"
echo ""

# Step 5: Deliver Order (This should create stock movements)
echo "Step 5: Delivering Order..."
DELIVERED=$(curl -s -X PUT "http://localhost:8080/api/v1/orders/$ORDER_ID/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "DELIVERED"}')

echo "$DELIVERED" | jq '.'
echo "✓ Status: $(echo "$DELIVERED" | jq -r '.status')"
echo ""

# Step 6: Check Stock Movements
echo "Step 6: Checking Stock Movements..."
MOVEMENTS=$(curl -s "http://localhost:8080/api/v1/stock/movements")
echo "$MOVEMENTS" | jq '.'
MOVEMENT_COUNT=$(echo "$MOVEMENTS" | jq -r '.totalElements')
echo "✓ Total Movements: $MOVEMENT_COUNT"
echo ""

# Step 7: Check Product Stock
echo "Step 7: Checking Product Stock..."
PRODUCT_UPDATED=$(curl -s "http://localhost:8080/api/v1/products/$PRODUCT_ID")
echo "$PRODUCT_UPDATED" | jq '.'
STOCK=$(echo "$PRODUCT_UPDATED" | jq -r '.stockQuantity')
AVG_COST=$(echo "$PRODUCT_UPDATED" | jq -r '.averageCost')
echo "✓ Stock Quantity: $STOCK (Expected: 50)"
echo "✓ Average Cost: $AVG_COST (Expected: 100.00)"
echo ""

# Step 8: Check Product Movement History
echo "Step 8: Checking Product Movement History..."
HISTORY=$(curl -s "http://localhost:8080/api/v1/stock/products/$PRODUCT_ID/history")
echo "$HISTORY" | jq '.'
echo ""

echo "=========================================="
echo "TEST COMPLETE!"
echo "=========================================="
