#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8080/api/v1"

echo "=========================================="
echo "PRODUCT MODULE - CRUD TESTS"
echo "=========================================="
echo ""

# Setup: Create a supplier first
echo -e "${BLUE}Setup: Creating Supplier${NC}"
SUPPLIER=$(curl -s -X POST "$BASE_URL/suppliers" \
  -H "Content-Type: application/json" \
  -d '{
    "company": "Product Test Supplier",
    "address": "123 Test St",
    "contact": "Jane Doe",
    "email": "jane@test.com",
    "phone": "+212522333444",
    "city": "Casablanca",
    "ice": "001234567890222"
  }')
SUPPLIER_ID=$(echo $SUPPLIER | jq -r '.id')
echo -e "${GREEN}✓ Supplier created (ID: $SUPPLIER_ID)${NC}"
echo ""

# Test 1: Create Product
echo -e "${BLUE}Test 1: Create Product${NC}"
PRODUCT=$(curl -s -X POST "$BASE_URL/products" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Premium Cotton Fabric",
    "description": "High quality cotton fabric for professional clothing",
    "price": 150.00,
    "category": "TEXTILE",
    "unit": "metre",
    "stockQuantity": 0,
    "supplierId": '$SUPPLIER_ID'
  }')

PRODUCT_ID=$(echo $PRODUCT | jq -r '.id')
if [ "$PRODUCT_ID" != "null" ] && [ -n "$PRODUCT_ID" ]; then
    echo -e "${GREEN}✓ Product created successfully (ID: $PRODUCT_ID)${NC}"
    echo "$PRODUCT" | jq '{id, name, price, category, stockQuantity}'
else
    echo -e "${RED}✗ Failed to create product${NC}"
    echo "$PRODUCT" | jq '.'
    exit 1
fi
echo ""

# Test 2: Get Product by ID
echo -e "${BLUE}Test 2: Get Product by ID${NC}"
PRODUCT_GET=$(curl -s "$BASE_URL/products/$PRODUCT_ID")
GET_ID=$(echo $PRODUCT_GET | jq -r '.id')

if [ "$GET_ID" == "$PRODUCT_ID" ]; then
    echo -e "${GREEN}✓ Product retrieved successfully${NC}"
    echo "$PRODUCT_GET" | jq '{id, name, price, stockQuantity, averageCost}'
else
    echo -e "${RED}✗ Failed to retrieve product${NC}"
fi
echo ""

# Test 3: List All Products (Paginated)
echo -e "${BLUE}Test 3: List All Products (Paginated)${NC}"
PRODUCTS_LIST=$(curl -s "$BASE_URL/products?page=0&size=10")
TOTAL=$(echo $PRODUCTS_LIST | jq -r '.totalElements')

if [ "$TOTAL" -ge 1 ]; then
    echo -e "${GREEN}✓ Products listed successfully (Total: $TOTAL)${NC}"
    echo "$PRODUCTS_LIST" | jq '.content[0:2] | .[] | {id, name, price, stockQuantity}'
else
    echo -e "${RED}✗ Failed to list products${NC}"
fi
echo ""

# Test 4: Update Product
echo -e "${BLUE}Test 4: Update Product${NC}"
PRODUCT_UPDATED=$(curl -s -X PUT "$BASE_URL/products/$PRODUCT_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Premium Cotton Fabric - Updated",
    "description": "Updated description",
    "price": 175.00,
    "category": "TEXTILE",
    "unit": "metre",
    "stockQuantity": 0,
    "supplierId": '$SUPPLIER_ID'
  }')

UPDATED_PRICE=$(echo $PRODUCT_UPDATED | jq -r '.price')
if [ "$UPDATED_PRICE" == "175.0" ]; then
    echo -e "${GREEN}✓ Product updated successfully${NC}"
    echo "$PRODUCT_UPDATED" | jq '{id, name, price}'
else
    echo -e "${RED}✗ Failed to update product${NC}"
fi
echo ""

# Test 5: Search by Name
echo -e "${BLUE}Test 5: Search Products by Name${NC}"
SEARCH_RESULT=$(curl -s "$BASE_URL/products/search?name=Cotton&page=0&size=10")
SEARCH_COUNT=$(echo $SEARCH_RESULT | jq -r '.totalElements')

if [ "$SEARCH_COUNT" -ge 1 ]; then
    echo -e "${GREEN}✓ Search working (Found: $SEARCH_COUNT products)${NC}"
    echo "$SEARCH_RESULT" | jq '.content[0] | {id, name}'
else
    echo -e "${RED}✗ Search failed or no results${NC}"
fi
echo ""

# Test 6: Filter by Category
echo -e "${BLUE}Test 6: Filter Products by Category${NC}"
CATEGORY_RESULT=$(curl -s "$BASE_URL/products/category/TEXTILE?page=0&size=10")
CATEGORY_COUNT=$(echo $CATEGORY_RESULT | jq -r '.totalElements')

if [ "$CATEGORY_COUNT" -ge 1 ]; then
    echo -e "${GREEN}✓ Category filter working (Found: $CATEGORY_COUNT products)${NC}"
    echo "$CATEGORY_RESULT" | jq '.content[0] | {id, name, category}'
else
    echo -e "${RED}✗ Category filter failed${NC}"
fi
echo ""

# Test 7: Filter by Supplier
echo -e "${BLUE}Test 7: Filter Products by Supplier${NC}"
SUPPLIER_RESULT=$(curl -s "$BASE_URL/products/supplier/$SUPPLIER_ID?page=0&size=10")
SUPPLIER_COUNT=$(echo $SUPPLIER_RESULT | jq -r '.totalElements')

if [ "$SUPPLIER_COUNT" -ge 1 ]; then
    echo -e "${GREEN}✓ Supplier filter working (Found: $SUPPLIER_COUNT products)${NC}"
    echo "$SUPPLIER_RESULT" | jq '.content[0] | {id, name, supplierId}'
else
    echo -e "${RED}✗ Supplier filter failed${NC}"
fi
echo ""

# Test 8: Delete Product
echo -e "${BLUE}Test 8: Delete Product${NC}"
DELETE_RESPONSE=$(curl -s -w "%{http_code}" -o /dev/null -X DELETE "$BASE_URL/products/$PRODUCT_ID")

if [ "$DELETE_RESPONSE" == "204" ] || [ "$DELETE_RESPONSE" == "200" ]; then
    echo -e "${GREEN}✓ Product deleted successfully${NC}"
else
    echo -e "${RED}✗ Failed to delete product (HTTP $DELETE_RESPONSE)${NC}"
fi
echo ""

# Cleanup: Delete supplier
echo -e "${BLUE}Cleanup: Deleting Supplier${NC}"
curl -s -X DELETE "$BASE_URL/suppliers/$SUPPLIER_ID" > /dev/null
echo -e "${GREEN}✓ Cleanup completed${NC}"
echo ""

echo "=========================================="
echo -e "${GREEN}PRODUCT MODULE TESTS COMPLETED${NC}"
echo "=========================================="
