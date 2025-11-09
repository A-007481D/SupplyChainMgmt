#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8080/api/v1"

echo "=========================================="
echo "ORDER MODULE - CRUD TESTS"
echo "=========================================="
echo ""

# Setup: Create supplier and products
echo -e "${BLUE}Setup: Creating Supplier and Products${NC}"
SUPPLIER=$(curl -s -X POST "$BASE_URL/suppliers" \
  -H "Content-Type: application/json" \
  -d '{
    "company": "Order Test Supplier",
    "address": "789 Order St",
    "contact": "Bob Johnson",
    "email": "bob@ordertest.com",
    "phone": "+212522555666",
    "city": "Casablanca",
    "ice": "001234567890333"
  }')
SUPPLIER_ID=$(echo $SUPPLIER | jq -r '.id')
echo -e "${GREEN}✓ Supplier created (ID: $SUPPLIER_ID)${NC}"

PRODUCT1=$(curl -s -X POST "$BASE_URL/products" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product 1",
    "description": "Product for order testing",
    "price": 100.00,
    "category": "TEXTILE",
    "unit": "metre",
    "stockQuantity": 0,
    "supplierId": '$SUPPLIER_ID'
  }')
PRODUCT1_ID=$(echo $PRODUCT1 | jq -r '.id')
echo -e "${GREEN}✓ Product 1 created (ID: $PRODUCT1_ID)${NC}"

PRODUCT2=$(curl -s -X POST "$BASE_URL/products" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product 2",
    "description": "Another product for order testing",
    "price": 50.00,
    "category": "ACCESSORY",
    "unit": "piece",
    "stockQuantity": 0,
    "supplierId": '$SUPPLIER_ID'
  }')
PRODUCT2_ID=$(echo $PRODUCT2 | jq -r '.id')
echo -e "${GREEN}✓ Product 2 created (ID: $PRODUCT2_ID)${NC}"
echo ""

# Test 1: Create Order
echo -e "${BLUE}Test 1: Create Order with Multiple Items${NC}"
ORDER=$(curl -s -X POST "$BASE_URL/orders" \
  -H "Content-Type: application/json" \
  -d '{
    "supplierId": '$SUPPLIER_ID',
    "items": [
      {
        "productId": '$PRODUCT1_ID',
        "quantity": 100,
        "unitPrice": 100.00
      },
      {
        "productId": '$PRODUCT2_ID',
        "quantity": 200,
        "unitPrice": 50.00
      }
    ]
  }')

ORDER_ID=$(echo $ORDER | jq -r '.id')
ORDER_STATUS=$(echo $ORDER | jq -r '.status')
ORDER_TOTAL=$(echo $ORDER | jq -r '.totalAmount')

if [ "$ORDER_ID" != "null" ] && [ -n "$ORDER_ID" ]; then
    echo -e "${GREEN}✓ Order created successfully${NC}"
    echo "  ID: $ORDER_ID"
    echo "  Status: $ORDER_STATUS"
    echo "  Total: $ORDER_TOTAL MAD (Expected: 20000.00)"
else
    echo -e "${RED}✗ Failed to create order${NC}"
    echo "$ORDER" | jq '.'
    exit 1
fi
echo ""

# Test 2: Get Order by ID
echo -e "${BLUE}Test 2: Get Order by ID${NC}"
ORDER_GET=$(curl -s "$BASE_URL/orders/$ORDER_ID")
GET_ID=$(echo $ORDER_GET | jq -r '.id')

if [ "$GET_ID" == "$ORDER_ID" ]; then
    echo -e "${GREEN}✓ Order retrieved successfully${NC}"
    echo "$ORDER_GET" | jq '{id, status, totalAmount, items: .items | length}'
else
    echo -e "${RED}✗ Failed to retrieve order${NC}"
fi
echo ""

# Test 3: List All Orders (Paginated)
echo -e "${BLUE}Test 3: List All Orders (Paginated)${NC}"
ORDERS_LIST=$(curl -s "$BASE_URL/orders?page=0&size=10")
TOTAL=$(echo $ORDERS_LIST | jq -r '.totalElements')

if [ "$TOTAL" -ge 1 ]; then
    echo -e "${GREEN}✓ Orders listed successfully (Total: $TOTAL)${NC}"
    echo "$ORDERS_LIST" | jq '.content[0:2] | .[] | {id, status, totalAmount}'
else
    echo -e "${RED}✗ Failed to list orders${NC}"
fi
echo ""

# Test 4: Update Order Status - Validate
echo -e "${BLUE}Test 4: Update Order Status to VALIDATED${NC}"
ORDER_VALIDATED=$(curl -s -X PUT "$BASE_URL/orders/$ORDER_ID/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "VALIDATED"}')

VALIDATED_STATUS=$(echo $ORDER_VALIDATED | jq -r '.status')
if [ "$VALIDATED_STATUS" == "VALIDATED" ]; then
    echo -e "${GREEN}✓ Order validated successfully${NC}"
    echo "  Status: $VALIDATED_STATUS"
else
    echo -e "${RED}✗ Failed to validate order${NC}"
    echo "$ORDER_VALIDATED" | jq '.'
fi
echo ""

# Test 5: Update Order Status - Deliver
echo -e "${BLUE}Test 5: Update Order Status to DELIVERED${NC}"
ORDER_DELIVERED=$(curl -s -X PUT "$BASE_URL/orders/$ORDER_ID/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "DELIVERED"}')

DELIVERED_STATUS=$(echo $ORDER_DELIVERED | jq -r '.status')
if [ "$DELIVERED_STATUS" == "DELIVERED" ]; then
    echo -e "${GREEN}✓ Order delivered successfully${NC}"
    echo "  Status: $DELIVERED_STATUS"
    echo "  Note: Stock movements should be created automatically"
else
    echo -e "${RED}✗ Failed to deliver order${NC}"
    echo "$ORDER_DELIVERED" | jq '.'
fi
echo ""

# Test 6: Verify Stock Movements Created
echo -e "${BLUE}Test 6: Verify Stock Movements Created${NC}"
sleep 1  # Give it a moment to process
MOVEMENTS=$(curl -s "$BASE_URL/stock/movements")
MOVEMENT_COUNT=$(echo $MOVEMENTS | jq -r '.totalElements')

if [ "$MOVEMENT_COUNT" -ge 2 ]; then
    echo -e "${GREEN}✓ Stock movements created (Count: $MOVEMENT_COUNT)${NC}"
    echo "$MOVEMENTS" | jq '.content[0:2] | .[] | {id, productId, type, quantity}'
else
    echo -e "${RED}✗ Stock movements not created or insufficient${NC}"
fi
echo ""

# Test 7: Verify Product Stock Updated
echo -e "${BLUE}Test 7: Verify Product Stock Updated${NC}"
PRODUCT1_UPDATED=$(curl -s "$BASE_URL/products/$PRODUCT1_ID")
STOCK1=$(echo $PRODUCT1_UPDATED | jq -r '.stockQuantity')
AVG_COST1=$(echo $PRODUCT1_UPDATED | jq -r '.averageCost')

if [ "$STOCK1" == "100" ]; then
    echo -e "${GREEN}✓ Product 1 stock updated correctly${NC}"
    echo "  Stock: $STOCK1 (Expected: 100)"
    echo "  Average Cost: $AVG_COST1"
else
    echo -e "${RED}✗ Product 1 stock not updated correctly (Got: $STOCK1, Expected: 100)${NC}"
fi

PRODUCT2_UPDATED=$(curl -s "$BASE_URL/products/$PRODUCT2_ID")
STOCK2=$(echo $PRODUCT2_UPDATED | jq -r '.stockQuantity')
AVG_COST2=$(echo $PRODUCT2_UPDATED | jq -r '.averageCost')

if [ "$STOCK2" == "200" ]; then
    echo -e "${GREEN}✓ Product 2 stock updated correctly${NC}"
    echo "  Stock: $STOCK2 (Expected: 200)"
    echo "  Average Cost: $AVG_COST2"
else
    echo -e "${RED}✗ Product 2 stock not updated correctly (Got: $STOCK2, Expected: 200)${NC}"
fi
echo ""

# Test 8: Test Invalid Status Transition
echo -e "${BLUE}Test 8: Test Invalid Status Transition${NC}"
INVALID_TRANSITION=$(curl -s -X PUT "$BASE_URL/orders/$ORDER_ID/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "WAITING"}')

ERROR_CODE=$(echo $INVALID_TRANSITION | jq -r '.status // .code')
if [ "$ERROR_CODE" != "null" ] && [ "$ERROR_CODE" != "WAITING" ]; then
    echo -e "${GREEN}✓ Invalid transition correctly rejected${NC}"
    echo "  Error: $(echo $INVALID_TRANSITION | jq -r '.message // .error')"
else
    echo -e "${RED}✗ Invalid transition was allowed (should be rejected)${NC}"
fi
echo ""

# Test 9: Create and Cancel Order
echo -e "${BLUE}Test 9: Create and Cancel Order${NC}"
ORDER2=$(curl -s -X POST "$BASE_URL/orders" \
  -H "Content-Type: application/json" \
  -d '{
    "supplierId": '$SUPPLIER_ID',
    "items": [
      {
        "productId": '$PRODUCT1_ID',
        "quantity": 50,
        "unitPrice": 100.00
      }
    ]
  }')
ORDER2_ID=$(echo $ORDER2 | jq -r '.id')

ORDER2_CANCELLED=$(curl -s -X PUT "$BASE_URL/orders/$ORDER2_ID/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "CANCELLED"}')

CANCELLED_STATUS=$(echo $ORDER2_CANCELLED | jq -r '.status')
if [ "$CANCELLED_STATUS" == "CANCELLED" ]; then
    echo -e "${GREEN}✓ Order cancelled successfully${NC}"
    echo "  Order ID: $ORDER2_ID"
    echo "  Status: $CANCELLED_STATUS"
else
    echo -e "${RED}✗ Failed to cancel order${NC}"
fi
echo ""

# Cleanup
echo -e "${BLUE}Cleanup: Deleting Test Data${NC}"
curl -s -X DELETE "$BASE_URL/products/$PRODUCT1_ID" > /dev/null
curl -s -X DELETE "$BASE_URL/products/$PRODUCT2_ID" > /dev/null
curl -s -X DELETE "$BASE_URL/suppliers/$SUPPLIER_ID" > /dev/null
echo -e "${GREEN}✓ Cleanup completed${NC}"
echo ""

echo "=========================================="
echo -e "${GREEN}ORDER MODULE TESTS COMPLETED${NC}"
echo "=========================================="
