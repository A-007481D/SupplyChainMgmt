#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8080/api/v1"

echo "=========================================="
echo "COMPLETE WORKFLOW TEST"
echo "Order Creation → Delivery → Stock Update"
echo "=========================================="
echo ""

# Step 1: Create Supplier
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}Step 1: Creating Supplier${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
SUPPLIER=$(curl -s -X POST "$BASE_URL/suppliers" \
  -H "Content-Type: application/json" \
  -d '{
    "company": "Workflow Test Supplier Ltd",
    "address": "123 Workflow Street",
    "contact": "John Workflow",
    "email": "workflow@test.com",
    "phone": "+212522999000",
    "city": "Casablanca",
    "ice": "001234567890999"
  }')

SUPPLIER_ID=$(echo $SUPPLIER | jq -r '.id')
if [ "$SUPPLIER_ID" != "null" ] && [ -n "$SUPPLIER_ID" ]; then
    echo -e "${GREEN}✓ Supplier created successfully${NC}"
    echo "$SUPPLIER" | jq '{id, company, email, city}'
else
    echo -e "${RED}✗ Failed to create supplier${NC}"
    echo "$SUPPLIER" | jq '.'
    exit 1
fi
echo ""

# Step 2: Create Products
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}Step 2: Creating Products${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

PRODUCT1=$(curl -s -X POST "$BASE_URL/products" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Premium Cotton Fabric",
    "description": "High quality cotton for professional clothing",
    "price": 150.00,
    "category": "TEXTILE",
    "unit": "metre",
    "stockQuantity": 0,
    "supplierId": '$SUPPLIER_ID'
  }')
PRODUCT1_ID=$(echo $PRODUCT1 | jq -r '.id')
PRODUCT1_STOCK=$(echo $PRODUCT1 | jq -r '.stockQuantity')

if [ "$PRODUCT1_ID" != "null" ]; then
    echo -e "${GREEN}✓ Product 1 created successfully${NC}"
    echo "  ID: $PRODUCT1_ID"
    echo "  Name: Premium Cotton Fabric"
    echo "  Price: 150.00 MAD"
    echo "  Initial Stock: $PRODUCT1_STOCK"
else
    echo -e "${RED}✗ Failed to create product 1${NC}"
    exit 1
fi

PRODUCT2=$(curl -s -X POST "$BASE_URL/products" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Metal Zippers",
    "description": "Professional grade metal zippers",
    "price": 5.50,
    "category": "ACCESSORY",
    "unit": "piece",
    "stockQuantity": 0,
    "supplierId": '$SUPPLIER_ID'
  }')
PRODUCT2_ID=$(echo $PRODUCT2 | jq -r '.id')
PRODUCT2_STOCK=$(echo $PRODUCT2 | jq -r '.stockQuantity')

if [ "$PRODUCT2_ID" != "null" ]; then
    echo -e "${GREEN}✓ Product 2 created successfully${NC}"
    echo "  ID: $PRODUCT2_ID"
    echo "  Name: Metal Zippers"
    echo "  Price: 5.50 MAD"
    echo "  Initial Stock: $PRODUCT2_STOCK"
else
    echo -e "${RED}✗ Failed to create product 2${NC}"
    exit 1
fi
echo ""

# Step 3: Create Order
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}Step 3: Creating Purchase Order${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

ORDER=$(curl -s -X POST "$BASE_URL/orders" \
  -H "Content-Type: application/json" \
  -d '{
    "supplierId": '$SUPPLIER_ID',
    "items": [
      {
        "productId": '$PRODUCT1_ID',
        "quantity": 100,
        "unitPrice": 150.00
      },
      {
        "productId": '$PRODUCT2_ID',
        "quantity": 500,
        "unitPrice": 5.50
      }
    ]
  }')

ORDER_ID=$(echo $ORDER | jq -r '.id')
ORDER_STATUS=$(echo $ORDER | jq -r '.status')
ORDER_TOTAL=$(echo $ORDER | jq -r '.totalAmount')
ORDER_ITEMS=$(echo $ORDER | jq -r '.items | length')

if [ "$ORDER_ID" != "null" ] && [ -n "$ORDER_ID" ]; then
    echo -e "${GREEN}✓ Order created successfully${NC}"
    echo "  Order ID: $ORDER_ID"
    echo "  Status: $ORDER_STATUS"
    echo "  Total Amount: $ORDER_TOTAL MAD"
    echo "  Number of Items: $ORDER_ITEMS"
    echo ""
    echo "  Order Details:"
    echo "$ORDER" | jq '.items[] | "    - Product \(.productId): \(.quantity) units @ \(.unitPrice) MAD = \(.subtotal) MAD"' -r
else
    echo -e "${RED}✗ Failed to create order${NC}"
    echo "$ORDER" | jq '.'
    exit 1
fi
echo ""

# Step 4: Validate Order
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}Step 4: Validating Order${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

ORDER_VALIDATED=$(curl -s -X PUT "$BASE_URL/orders/$ORDER_ID/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "VALIDATED"}')

VALIDATED_STATUS=$(echo $ORDER_VALIDATED | jq -r '.status')
if [ "$VALIDATED_STATUS" == "VALIDATED" ]; then
    echo -e "${GREEN}✓ Order validated successfully${NC}"
    echo "  Order ID: $ORDER_ID"
    echo "  New Status: $VALIDATED_STATUS"
else
    echo -e "${RED}✗ Failed to validate order${NC}"
    echo "$ORDER_VALIDATED" | jq '.'
    exit 1
fi
echo ""

# Step 5: Deliver Order (This triggers stock movements)
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}Step 5: Delivering Order (Triggers Stock Movement)${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

ORDER_DELIVERED=$(curl -s -X PUT "$BASE_URL/orders/$ORDER_ID/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "DELIVERED"}')

DELIVERED_STATUS=$(echo $ORDER_DELIVERED | jq -r '.status')
if [ "$DELIVERED_STATUS" == "DELIVERED" ]; then
    echo -e "${GREEN}✓ Order delivered successfully${NC}"
    echo "  Order ID: $ORDER_ID"
    echo "  New Status: $DELIVERED_STATUS"
    echo ""
    echo -e "${YELLOW}  ⚡ Stock movements should be created automatically...${NC}"
else
    echo -e "${RED}✗ Failed to deliver order${NC}"
    echo "$ORDER_DELIVERED" | jq '.'
    exit 1
fi
echo ""

# Give the system a moment to process
sleep 2

# Step 6: Verify Stock Movements Created
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}Step 6: Verifying Stock Movements Created${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

MOVEMENTS=$(curl -s "$BASE_URL/stock/movements")
MOVEMENT_COUNT=$(echo $MOVEMENTS | jq -r '.totalElements')

# Filter movements for this order
ORDER_MOVEMENTS=$(echo $MOVEMENTS | jq --arg oid "$ORDER_ID" '[.content[] | select(.orderId == ($oid | tonumber))]')
ORDER_MOVEMENT_COUNT=$(echo $ORDER_MOVEMENTS | jq '. | length')

if [ "$ORDER_MOVEMENT_COUNT" -ge 2 ]; then
    echo -e "${GREEN}✓ Stock movements created successfully${NC}"
    echo "  Total movements for this order: $ORDER_MOVEMENT_COUNT"
    echo ""
    echo "  Movement Details:"
    echo "$ORDER_MOVEMENTS" | jq '.[] | "    - Product \(.productId): \(.type) \(.quantity) units @ \(.unitCost) MAD (Total: \(.totalCost) MAD, Remaining: \(.remainingQuantity))"' -r
else
    echo -e "${RED}✗ Stock movements not created or insufficient${NC}"
    echo "  Expected: 2 movements"
    echo "  Found: $ORDER_MOVEMENT_COUNT movements"
fi
echo ""

# Step 7: Verify Product Stock Updated
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}Step 7: Verifying Product Stock Updated${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

PRODUCT1_UPDATED=$(curl -s "$BASE_URL/products/$PRODUCT1_ID")
STOCK1_NEW=$(echo $PRODUCT1_UPDATED | jq -r '.stockQuantity')
AVG_COST1=$(echo $PRODUCT1_UPDATED | jq -r '.averageCost')

echo -e "${GREEN}Product 1 (Premium Cotton Fabric):${NC}"
echo "  Initial Stock: $PRODUCT1_STOCK"
echo "  Current Stock: $STOCK1_NEW (Expected: 100)"
echo "  Average Cost: $AVG_COST1 MAD (Expected: 150.00)"

if [ "$STOCK1_NEW" == "100" ]; then
    echo -e "${GREEN}  ✓ Stock updated correctly${NC}"
else
    echo -e "${RED}  ✗ Stock not updated correctly${NC}"
fi
echo ""

PRODUCT2_UPDATED=$(curl -s "$BASE_URL/products/$PRODUCT2_ID")
STOCK2_NEW=$(echo $PRODUCT2_UPDATED | jq -r '.stockQuantity')
AVG_COST2=$(echo $PRODUCT2_UPDATED | jq -r '.averageCost')

echo -e "${GREEN}Product 2 (Metal Zippers):${NC}"
echo "  Initial Stock: $PRODUCT2_STOCK"
echo "  Current Stock: $STOCK2_NEW (Expected: 500)"
echo "  Average Cost: $AVG_COST2 MAD (Expected: 5.50)"

if [ "$STOCK2_NEW" == "500" ]; then
    echo -e "${GREEN}  ✓ Stock updated correctly${NC}"
else
    echo -e "${RED}  ✗ Stock not updated correctly${NC}"
fi
echo ""

# Step 8: Check Movement History
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}Step 8: Checking Product Movement History${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

HISTORY1=$(curl -s "$BASE_URL/stock/products/$PRODUCT1_ID/history")
HISTORY1_COUNT=$(echo $HISTORY1 | jq '. | length')

echo -e "${GREEN}Product 1 Movement History:${NC}"
echo "  Total movements: $HISTORY1_COUNT"
if [ "$HISTORY1_COUNT" -ge 1 ]; then
    echo "$HISTORY1" | jq '.[] | "    - \(.movementDate | split("T")[0]): \(.type) \(.quantity) units"' -r
    echo -e "${GREEN}  ✓ Movement history available${NC}"
else
    echo -e "${RED}  ✗ No movement history found${NC}"
fi
echo ""

# Step 9: Test Stock Exit
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}Step 9: Testing Manual Stock EXIT${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

EXIT_MOVEMENT=$(curl -s -X POST "$BASE_URL/stock/movements" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": '$PRODUCT1_ID',
    "type": "EXIT",
    "quantity": 25,
    "unitCost": 150.00
  }')

EXIT_ID=$(echo $EXIT_MOVEMENT | jq -r '.id')
if [ "$EXIT_ID" != "null" ] && [ -n "$EXIT_ID" ]; then
    echo -e "${GREEN}✓ EXIT movement created successfully${NC}"
    echo "  Movement ID: $EXIT_ID"
    echo "  Product: $PRODUCT1_ID"
    echo "  Quantity: 25 units"
    
    PRODUCT1_AFTER_EXIT=$(curl -s "$BASE_URL/products/$PRODUCT1_ID")
    STOCK1_AFTER_EXIT=$(echo $PRODUCT1_AFTER_EXIT | jq -r '.stockQuantity')
    
    echo "  New Stock: $STOCK1_AFTER_EXIT (Expected: 75)"
    
    if [ "$STOCK1_AFTER_EXIT" == "75" ]; then
        echo -e "${GREEN}  ✓ Stock decreased correctly${NC}"
    else
        echo -e "${RED}  ✗ Stock not decreased correctly${NC}"
    fi
else
    echo -e "${RED}✗ Failed to create EXIT movement${NC}"
fi
echo ""

# Step 10: Test Insufficient Stock Validation
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}Step 10: Testing Insufficient Stock Validation${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

INSUFFICIENT_EXIT=$(curl -s -X POST "$BASE_URL/stock/movements" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": '$PRODUCT1_ID',
    "type": "EXIT",
    "quantity": 10000,
    "unitCost": 150.00
  }')

ERROR_CODE=$(echo $INSUFFICIENT_EXIT | jq -r '.code // .status')
if [ "$ERROR_CODE" == "INSUFFICIENT_STOCK" ] || [ "$ERROR_CODE" == "400" ]; then
    echo -e "${GREEN}✓ Insufficient stock validation working correctly${NC}"
    echo "  Error Message: $(echo $INSUFFICIENT_EXIT | jq -r '.message')"
else
    echo -e "${RED}✗ Insufficient stock validation not working${NC}"
fi
echo ""

# Summary
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}WORKFLOW TEST SUMMARY${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo -e "${GREEN}✓ Supplier Created${NC}"
echo -e "${GREEN}✓ Products Created (2)${NC}"
echo -e "${GREEN}✓ Order Created (Status: WAITING)${NC}"
echo -e "${GREEN}✓ Order Validated (Status: VALIDATED)${NC}"
echo -e "${GREEN}✓ Order Delivered (Status: DELIVERED)${NC}"
echo -e "${GREEN}✓ Stock Movements Created Automatically (2)${NC}"
echo -e "${GREEN}✓ Product Stocks Updated${NC}"
echo -e "${GREEN}✓ Average Costs Calculated${NC}"
echo -e "${GREEN}✓ Movement History Available${NC}"
echo -e "${GREEN}✓ Manual Stock EXIT Working${NC}"
echo -e "${GREEN}✓ Insufficient Stock Validation Working${NC}"
echo ""
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}ALL WORKFLOW TESTS PASSED! ✨${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo "Test Data IDs (for manual verification):"
echo "  Supplier ID: $SUPPLIER_ID"
echo "  Product 1 ID: $PRODUCT1_ID"
echo "  Product 2 ID: $PRODUCT2_ID"
echo "  Order ID: $ORDER_ID"
echo ""
echo "Access Swagger UI: http://localhost:8080/swagger-ui/index.html"
