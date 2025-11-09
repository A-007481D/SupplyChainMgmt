#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8080/api/v1"

echo "=========================================="
echo "STOCK MODULE - MOVEMENT TESTS"
echo "=========================================="
echo ""

# Setup: Create supplier and product
echo -e "${BLUE}Setup: Creating Supplier and Product${NC}"
SUPPLIER=$(curl -s -X POST "$BASE_URL/suppliers" \
  -H "Content-Type: application/json" \
  -d '{
    "company": "Stock Test Supplier",
    "address": "999 Stock St",
    "contact": "Alice Brown",
    "email": "alice@stocktest.com",
    "phone": "+212522777888",
    "city": "Casablanca",
    "ice": "001234567890444"
  }')
SUPPLIER_ID=$(echo $SUPPLIER | jq -r '.id')
echo -e "${GREEN}✓ Supplier created (ID: $SUPPLIER_ID)${NC}"

PRODUCT=$(curl -s -X POST "$BASE_URL/products" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Stock Test Product",
    "description": "Product for stock testing",
    "price": 200.00,
    "category": "TEXTILE",
    "unit": "metre",
    "stockQuantity": 0,
    "supplierId": '$SUPPLIER_ID'
  }')
PRODUCT_ID=$(echo $PRODUCT | jq -r '.id')
echo -e "${GREEN}✓ Product created (ID: $PRODUCT_ID, Initial Stock: 0)${NC}"
echo ""

# Test 1: Manual Stock Entry
echo -e "${BLUE}Test 1: Create Manual Stock ENTRY Movement${NC}"
ENTRY_MOVEMENT=$(curl -s -X POST "$BASE_URL/stock/movements" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": '$PRODUCT_ID',
    "type": "ENTRY",
    "quantity": 100,
    "unitCost": 200.00
  }')

ENTRY_ID=$(echo $ENTRY_MOVEMENT | jq -r '.id')
if [ "$ENTRY_ID" != "null" ] && [ -n "$ENTRY_ID" ]; then
    echo -e "${GREEN}✓ ENTRY movement created successfully${NC}"
    echo "$ENTRY_MOVEMENT" | jq '{id, productId, type, quantity, unitCost, totalCost}'
else
    echo -e "${RED}✗ Failed to create ENTRY movement${NC}"
    echo "$ENTRY_MOVEMENT" | jq '.'
fi
echo ""

# Test 2: Verify Stock Updated After Entry
echo -e "${BLUE}Test 2: Verify Stock Updated After ENTRY${NC}"
PRODUCT_AFTER_ENTRY=$(curl -s "$BASE_URL/products/$PRODUCT_ID")
STOCK_AFTER_ENTRY=$(echo $PRODUCT_AFTER_ENTRY | jq -r '.stockQuantity')
AVG_COST=$(echo $PRODUCT_AFTER_ENTRY | jq -r '.averageCost')

if [ "$STOCK_AFTER_ENTRY" == "100" ]; then
    echo -e "${GREEN}✓ Stock updated correctly after ENTRY${NC}"
    echo "  Stock: $STOCK_AFTER_ENTRY (Expected: 100)"
    echo "  Average Cost: $AVG_COST (Expected: 200.00)"
else
    echo -e "${RED}✗ Stock not updated correctly (Got: $STOCK_AFTER_ENTRY, Expected: 100)${NC}"
fi
echo ""

# Test 3: Manual Stock Exit
echo -e "${BLUE}Test 3: Create Manual Stock EXIT Movement${NC}"
EXIT_MOVEMENT=$(curl -s -X POST "$BASE_URL/stock/movements" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": '$PRODUCT_ID',
    "type": "EXIT",
    "quantity": 30,
    "unitCost": 200.00
  }')

EXIT_ID=$(echo $EXIT_MOVEMENT | jq -r '.id')
if [ "$EXIT_ID" != "null" ] && [ -n "$EXIT_ID" ]; then
    echo -e "${GREEN}✓ EXIT movement created successfully${NC}"
    echo "$EXIT_MOVEMENT" | jq '{id, productId, type, quantity, unitCost}'
else
    echo -e "${RED}✗ Failed to create EXIT movement${NC}"
    echo "$EXIT_MOVEMENT" | jq '.'
fi
echo ""

# Test 4: Verify Stock Updated After Exit
echo -e "${BLUE}Test 4: Verify Stock Updated After EXIT${NC}"
PRODUCT_AFTER_EXIT=$(curl -s "$BASE_URL/products/$PRODUCT_ID")
STOCK_AFTER_EXIT=$(echo $PRODUCT_AFTER_EXIT | jq -r '.stockQuantity')

if [ "$STOCK_AFTER_EXIT" == "70" ]; then
    echo -e "${GREEN}✓ Stock updated correctly after EXIT${NC}"
    echo "  Stock: $STOCK_AFTER_EXIT (Expected: 70)"
else
    echo -e "${RED}✗ Stock not updated correctly (Got: $STOCK_AFTER_EXIT, Expected: 70)${NC}"
fi
echo ""

# Test 5: Test Insufficient Stock Validation
echo -e "${BLUE}Test 5: Test Insufficient Stock Validation${NC}"
INSUFFICIENT_EXIT=$(curl -s -X POST "$BASE_URL/stock/movements" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": '$PRODUCT_ID',
    "type": "EXIT",
    "quantity": 1000,
    "unitCost": 200.00
  }')

ERROR_CODE=$(echo $INSUFFICIENT_EXIT | jq -r '.code // .status')
if [ "$ERROR_CODE" == "INSUFFICIENT_STOCK" ] || [ "$ERROR_CODE" == "400" ]; then
    echo -e "${GREEN}✓ Insufficient stock validation working${NC}"
    echo "  Error: $(echo $INSUFFICIENT_EXIT | jq -r '.message')"
else
    echo -e "${RED}✗ Insufficient stock validation failed${NC}"
    echo "$INSUFFICIENT_EXIT" | jq '.'
fi
echo ""

# Test 6: List All Stock Movements
echo -e "${BLUE}Test 6: List All Stock Movements (Paginated)${NC}"
MOVEMENTS_LIST=$(curl -s "$BASE_URL/stock/movements?page=0&size=10")
TOTAL_MOVEMENTS=$(echo $MOVEMENTS_LIST | jq -r '.totalElements')

if [ "$TOTAL_MOVEMENTS" -ge 2 ]; then
    echo -e "${GREEN}✓ Stock movements listed successfully (Total: $TOTAL_MOVEMENTS)${NC}"
    echo "$MOVEMENTS_LIST" | jq '.content[0:2] | .[] | {id, productId, type, quantity, totalCost, remainingQuantity}'
else
    echo -e "${RED}✗ Failed to list stock movements${NC}"
fi
echo ""

# Test 7: Get Product Movement History
echo -e "${BLUE}Test 7: Get Product Movement History${NC}"
PRODUCT_HISTORY=$(curl -s "$BASE_URL/stock/products/$PRODUCT_ID/history")
HISTORY_COUNT=$(echo $PRODUCT_HISTORY | jq '. | length')

if [ "$HISTORY_COUNT" -ge 2 ]; then
    echo -e "${GREEN}✓ Product movement history retrieved (Count: $HISTORY_COUNT)${NC}"
    echo "$PRODUCT_HISTORY" | jq '[.[] | {id, type, quantity, movementDate}]'
else
    echo -e "${RED}✗ Failed to retrieve product history${NC}"
fi
echo ""

# Test 8: Test FIFO Remaining Quantity
echo -e "${BLUE}Test 8: Verify FIFO Remaining Quantity Tracking${NC}"
FIFO_MOVEMENTS=$(curl -s "$BASE_URL/stock/movements")
FIRST_ENTRY=$(echo $FIFO_MOVEMENTS | jq '.content[] | select(.type == "ENTRY" and .productId == '$PRODUCT_ID') | {id, quantity, remainingQuantity}' | head -1)

if [ -n "$FIRST_ENTRY" ]; then
    echo -e "${GREEN}✓ FIFO tracking data available${NC}"
    echo "$FIRST_ENTRY"
else
    echo -e "${RED}✗ FIFO tracking data not available${NC}"
fi
echo ""

# Test 9: Create Second Entry (Test CUMP/FIFO Calculation)
echo -e "${BLUE}Test 9: Create Second ENTRY (Different Price)${NC}"
ENTRY2_MOVEMENT=$(curl -s -X POST "$BASE_URL/stock/movements" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": '$PRODUCT_ID',
    "type": "ENTRY",
    "quantity": 50,
    "unitCost": 220.00
  }')

ENTRY2_ID=$(echo $ENTRY2_MOVEMENT | jq -r '.id')
if [ "$ENTRY2_ID" != "null" ] && [ -n "$ENTRY2_ID" ]; then
    echo -e "${GREEN}✓ Second ENTRY created successfully${NC}"
    
    # Check updated average cost
    PRODUCT_AFTER_ENTRY2=$(curl -s "$BASE_URL/products/$PRODUCT_ID")
    STOCK_FINAL=$(echo $PRODUCT_AFTER_ENTRY2 | jq -r '.stockQuantity')
    AVG_COST_FINAL=$(echo $PRODUCT_AFTER_ENTRY2 | jq -r '.averageCost')
    
    echo "  Final Stock: $STOCK_FINAL (Expected: 120)"
    echo "  Final Average Cost: $AVG_COST_FINAL"
    echo "  (FIFO: varies, CUMP: ~206.67)"
else
    echo -e "${RED}✗ Failed to create second ENTRY${NC}"
fi
echo ""

# Test 10: Stock Adjustment
echo -e "${BLUE}Test 10: Create Stock ADJUSTMENT Movement${NC}"
ADJUSTMENT=$(curl -s -X POST "$BASE_URL/stock/movements" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": '$PRODUCT_ID',
    "type": "ADJUSTMENT",
    "quantity": 5,
    "unitCost": 200.00
  }')

ADJ_ID=$(echo $ADJUSTMENT | jq -r '.id')
if [ "$ADJ_ID" != "null" ] && [ -n "$ADJ_ID" ]; then
    echo -e "${GREEN}✓ ADJUSTMENT movement created successfully${NC}"
    echo "$ADJUSTMENT" | jq '{id, type, quantity}'
else
    echo -e "${RED}✗ Failed to create ADJUSTMENT movement${NC}"
fi
echo ""

# Cleanup
echo -e "${BLUE}Cleanup: Deleting Test Data${NC}"
curl -s -X DELETE "$BASE_URL/products/$PRODUCT_ID" > /dev/null
curl -s -X DELETE "$BASE_URL/suppliers/$SUPPLIER_ID" > /dev/null
echo -e "${GREEN}✓ Cleanup completed${NC}"
echo ""

echo "=========================================="
echo -e "${GREEN}STOCK MODULE TESTS COMPLETED${NC}"
echo "=========================================="
