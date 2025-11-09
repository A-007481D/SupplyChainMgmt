#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8080/api/v1"

echo "=========================================="
echo "SUPPLIER MODULE - CRUD TESTS"
echo "=========================================="
echo ""

# Test 1: Create Supplier
echo -e "${BLUE}Test 1: Create Supplier${NC}"
SUPPLIER=$(curl -s -X POST "$BASE_URL/suppliers" \
  -H "Content-Type: application/json" \
  -d '{
    "company": "Test Supplier Co",
    "address": "123 Industrial Ave",
    "contact": "John Smith",
    "email": "john@testsupplier.com",
    "phone": "+212522111222",
    "city": "Casablanca",
    "ice": "001234567890111"
  }')

SUPPLIER_ID=$(echo $SUPPLIER | jq -r '.id')
if [ "$SUPPLIER_ID" != "null" ] && [ -n "$SUPPLIER_ID" ]; then
    echo -e "${GREEN}✓ Supplier created successfully (ID: $SUPPLIER_ID)${NC}"
else
    echo -e "${RED}✗ Failed to create supplier${NC}"
    echo "$SUPPLIER" | jq '.'
    exit 1
fi
echo ""

# Test 2: Get Supplier by ID
echo -e "${BLUE}Test 2: Get Supplier by ID${NC}"
SUPPLIER_GET=$(curl -s "$BASE_URL/suppliers/$SUPPLIER_ID")
GET_ID=$(echo $SUPPLIER_GET | jq -r '.id')

if [ "$GET_ID" == "$SUPPLIER_ID" ]; then
    echo -e "${GREEN}✓ Supplier retrieved successfully${NC}"
    echo "$SUPPLIER_GET" | jq '{id, company, email, city}'
else
    echo -e "${RED}✗ Failed to retrieve supplier${NC}"
fi
echo ""

# Test 3: List All Suppliers (Paginated)
echo -e "${BLUE}Test 3: List All Suppliers (Paginated)${NC}"
SUPPLIERS_LIST=$(curl -s "$BASE_URL/suppliers?page=0&size=10")
TOTAL=$(echo $SUPPLIERS_LIST | jq -r '.totalElements')

if [ "$TOTAL" -ge 1 ]; then
    echo -e "${GREEN}✓ Suppliers listed successfully (Total: $TOTAL)${NC}"
    echo "$SUPPLIERS_LIST" | jq '.content[0:2] | .[] | {id, company, city}'
else
    echo -e "${RED}✗ Failed to list suppliers${NC}"
fi
echo ""

# Test 4: Update Supplier
echo -e "${BLUE}Test 4: Update Supplier${NC}"
SUPPLIER_UPDATED=$(curl -s -X PUT "$BASE_URL/suppliers/$SUPPLIER_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "company": "Test Supplier Co - Updated",
    "address": "456 New Industrial Ave",
    "contact": "John Smith",
    "email": "john.updated@testsupplier.com",
    "phone": "+212522111222",
    "city": "Rabat",
    "ice": "001234567890111"
  }')

UPDATED_CITY=$(echo $SUPPLIER_UPDATED | jq -r '.city')
if [ "$UPDATED_CITY" == "Rabat" ]; then
    echo -e "${GREEN}✓ Supplier updated successfully${NC}"
    echo "$SUPPLIER_UPDATED" | jq '{id, company, city, email}'
else
    echo -e "${RED}✗ Failed to update supplier${NC}"
fi
echo ""

# Test 5: Search/Filter Test
echo -e "${BLUE}Test 5: Pagination Test${NC}"
PAGE1=$(curl -s "$BASE_URL/suppliers?page=0&size=2")
PAGE1_SIZE=$(echo $PAGE1 | jq -r '.numberOfElements')
TOTAL_PAGES=$(echo $PAGE1 | jq -r '.totalPages')

if [ "$PAGE1_SIZE" -ge 1 ]; then
    echo -e "${GREEN}✓ Pagination working (Page size: $PAGE1_SIZE, Total pages: $TOTAL_PAGES)${NC}"
else
    echo -e "${RED}✗ Pagination failed${NC}"
fi
echo ""

# Test 6: Delete Supplier
echo -e "${BLUE}Test 6: Delete Supplier${NC}"
DELETE_RESPONSE=$(curl -s -w "%{http_code}" -o /dev/null -X DELETE "$BASE_URL/suppliers/$SUPPLIER_ID")

if [ "$DELETE_RESPONSE" == "204" ] || [ "$DELETE_RESPONSE" == "200" ]; then
    echo -e "${GREEN}✓ Supplier deleted successfully${NC}"
else
    echo -e "${RED}✗ Failed to delete supplier (HTTP $DELETE_RESPONSE)${NC}"
fi
echo ""

# Test 7: Verify Deletion
echo -e "${BLUE}Test 7: Verify Deletion${NC}"
VERIFY_DELETE=$(curl -s -w "%{http_code}" -o /dev/null "$BASE_URL/suppliers/$SUPPLIER_ID")

if [ "$VERIFY_DELETE" == "404" ] || [ "$VERIFY_DELETE" == "400" ]; then
    echo -e "${GREEN}✓ Supplier deletion verified (not found)${NC}"
else
    echo -e "${RED}✗ Supplier still exists after deletion${NC}"
fi
echo ""

echo "=========================================="
echo -e "${GREEN}SUPPLIER MODULE TESTS COMPLETED${NC}"
echo "=========================================="
