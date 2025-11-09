#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "=========================================="
echo "RUNNING ALL MODULE TESTS"
echo "=========================================="
echo ""

# Check if server is running
echo -e "${BLUE}Checking if server is running...${NC}"
SERVER_CHECK=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/v1/suppliers)

if [ "$SERVER_CHECK" != "200" ]; then
    echo -e "${RED}✗ Server is not running on http://localhost:8080${NC}"
    echo "Please start the application first:"
    echo "  mvn spring-boot:run"
    exit 1
fi

echo -e "${GREEN}✓ Server is running${NC}"
echo ""

# Run Supplier Tests
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}Running Supplier Module Tests...${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
./scripts/test-suppliers.sh
SUPPLIER_RESULT=$?
echo ""

# Run Product Tests
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}Running Product Module Tests...${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
./scripts/test-products.sh
PRODUCT_RESULT=$?
echo ""

# Run Order Tests
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}Running Order Module Tests...${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
./scripts/test-orders.sh
ORDER_RESULT=$?
echo ""

# Run Stock Tests
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}Running Stock Module Tests...${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
./scripts/test-stock.sh
STOCK_RESULT=$?
echo ""

# Run Workflow Test
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}Running Complete Workflow Test...${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
./scripts/test-workflow.sh
WORKFLOW_RESULT=$?
echo ""

# Summary
echo "=========================================="
echo "TEST RESULTS SUMMARY"
echo "=========================================="
echo ""

if [ $SUPPLIER_RESULT -eq 0 ]; then
    echo -e "${GREEN}✓ Supplier Module Tests: PASSED${NC}"
else
    echo -e "${RED}✗ Supplier Module Tests: FAILED${NC}"
fi

if [ $PRODUCT_RESULT -eq 0 ]; then
    echo -e "${GREEN}✓ Product Module Tests: PASSED${NC}"
else
    echo -e "${RED}✗ Product Module Tests: FAILED${NC}"
fi

if [ $ORDER_RESULT -eq 0 ]; then
    echo -e "${GREEN}✓ Order Module Tests: PASSED${NC}"
else
    echo -e "${RED}✗ Order Module Tests: FAILED${NC}"
fi

if [ $STOCK_RESULT -eq 0 ]; then
    echo -e "${GREEN}✓ Stock Module Tests: PASSED${NC}"
else
    echo -e "${RED}✗ Stock Module Tests: FAILED${NC}"
fi

if [ $WORKFLOW_RESULT -eq 0 ]; then
    echo -e "${GREEN}✓ Complete Workflow Test: PASSED${NC}"
else
    echo -e "${RED}✗ Complete Workflow Test: FAILED${NC}"
fi

echo ""
echo "=========================================="

# Calculate overall result
TOTAL_FAILED=$((SUPPLIER_RESULT + PRODUCT_RESULT + ORDER_RESULT + STOCK_RESULT + WORKFLOW_RESULT))

if [ $TOTAL_FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 ALL TESTS PASSED! 🎉${NC}"
    echo "=========================================="
    exit 0
else
    echo -e "${RED}❌ SOME TESTS FAILED ❌${NC}"
    echo "=========================================="
    exit 1
fi
