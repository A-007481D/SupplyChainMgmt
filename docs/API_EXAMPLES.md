# API Examples - Tricol Supplier Order Management

Complete examples for all API endpoints with request/response samples.

---

## Base URL

```
http://localhost:8080/api/v1
```

---

## Suppliers API

### Create Supplier

**Endpoint:** `POST /api/v1/suppliers`

**Request:**
```bash
  curl -X POST http://localhost:8080/api/v1/suppliers \
  -H "Content-Type: application/json" \
  -d '{
    "company": "Textile Premium SA",
    "address": "Zone Industrielle Ain Sebaa",
    "contact": "Ahmed Benani",
    "email": "contact@textile-premium.ma",
    "phone": "+212522334455",
    "city": "Casablanca",
    "ice": "002345678901234"
  }'
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "company": "Textile Premium SA",
  "address": "Zone Industrielle Ain Sebaa",
  "contact": "Ahmed Benani",
  "email": "contact@textile-premium.ma",
  "phone": "+212522334455",
  "city": "Casablanca",
  "ice": "002345678901234"
}
```

### Get All Suppliers (Paginated)

**Endpoint:** `GET /api/v1/suppliers?page=0&size=10`

**Request:**
```bash
  curl http://localhost:8080/api/v1/suppliers?page=0&size=10
```

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "company": "Textile Premium SA",
      "email": "contact@textile-premium.ma",
      "city": "Casablanca"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

### Get Supplier by ID

**Endpoint:** `GET /api/v1/suppliers/{id}`

**Request:**
```bash
  curl http://localhost:8080/api/v1/suppliers/1
```

### Update Supplier

**Endpoint:** `PUT /api/v1/suppliers/{id}`

**Request:**
```bash
  curl -X PUT http://localhost:8080/api/v1/suppliers/1 \
  -H "Content-Type: application/json" \
  -d '{
    "company": "Textile Premium SA - Updated",
    "address": "New Address",
    "contact": "Ahmed Benani",
    "email": "new@textile-premium.ma",
    "phone": "+212522334455",
    "city": "Rabat",
    "ice": "002345678901234"
  }'
```

### Delete Supplier

**Endpoint:** `DELETE /api/v1/suppliers/{id}`

**Request:**
```bash
  curl -X DELETE http://localhost:8080/api/v1/suppliers/1
```

**Response:** `204 No Content`

---

##  Products API

### Create Product

**Endpoint:** `POST /api/v1/products`

**Request:**
```bash
  curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Premium Cotton Fabric",
    "description": "High quality cotton fabric",
    "price": 150.00,
    "category": "TEXTILE",
    "unit": "metre",
    "stockQuantity": 0,
    "supplierId": 1
  }'
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "name": "Premium Cotton Fabric",
  "description": "High quality cotton fabric",
  "price": 150.0,
  "category": "TEXTILE",
  "unit": "metre",
  "stockQuantity": 0,
  "supplierId": 1,
  "averageCost": null
}
```

**Valid Categories:**
- `TEXTILE` - Fabrics, threads, raw materials
- `ACCESSORY` - Buttons, zippers, labels
- `EQUIPMENT` - Machines, tools
- `PACKAGING` - Boxes, wrapping materials
- `SAFETY_GEAR` - Gloves, helmets, protective clothing
- `MAINTENANCE` - Cleaning supplies
- `OTHER` - Miscellaneous items

### Get All Products (Paginated)

**Endpoint:** `GET /api/v1/products?page=0&size=10`

**Request:**
```bash
  curl http://localhost:8080/api/v1/products?page=0&size=10
```

### Search Products by Name

**Endpoint:** `GET /api/v1/products/search?name={name}&page=0&size=10`

**Request:**
```bash
 curl http://localhost:8080/api/v1/products/search?name=Cotton&page=0&size=10
```

### Filter by Category

**Endpoint:** `GET /api/v1/products/category/{category}?page=0&size=10`

**Request:**
```bash
  curl http://localhost:8080/api/v1/products/category/TEXTILE?page=0&size=10
```

### Filter by Supplier

**Endpoint:** `GET /api/v1/products/supplier/{supplierId}?page=0&size=10`

**Request:**
```bash
  curl http://localhost:8080/api/v1/products/supplier/1?page=0&size=10
```

### Get Product by ID

**Endpoint:** `GET /api/v1/products/{id}`

**Request:**
```bash
  curl http://localhost:8080/api/v1/products/1
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "Premium Cotton Fabric",
  "description": "High quality cotton fabric",
  "price": 150.0,
  "category": "TEXTILE",
  "unit": "metre",
  "stockQuantity": 100,
  "supplierId": 1,
  "averageCost": 150.0
}
```

### Update Product

**Endpoint:** `PUT /api/v1/products/{id}`

### Delete Product

**Endpoint:** `DELETE /api/v1/products/{id}`

---

## Orders API

### Create Order

**Endpoint:** `POST /api/v1/orders`

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "supplierId": 1,
    "items": [
      {
        "productId": 1,
        "quantity": 100,
        "unitPrice": 150.00
      },
      {
        "productId": 2,
        "quantity": 500,
        "unitPrice": 5.50
      }
    ]
  }'
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "supplierId": 1,
  "orderDate": "2025-11-09T16:30:00Z",
  "status": "WAITING",
  "totalAmount": 17750.00,
  "items": [
    {
      "id": 1,
      "productId": 1,
      "quantity": 100,
      "unitPrice": 150.00,
      "subtotal": 15000.00
    },
    {
      "id": 2,
      "productId": 2,
      "quantity": 500,
      "unitPrice": 5.50,
      "subtotal": 2750.00
    }
  ]
}
```

### Get All Orders (Paginated)

**Endpoint:** `GET /api/v1/orders?page=0&size=10`

**Request:**
```bash
  curl http://localhost:8080/api/v1/orders?page=0&size=10
```

### Get Order by ID

**Endpoint:** `GET /api/v1/orders/{id}`

**Request:**
```bash
  curl http://localhost:8080/api/v1/orders/1
```

### Update Order Status

**Endpoint:** `PUT /api/v1/orders/{id}/status`

#### Validate Order

**Request:**
```bash
curl -X PUT http://localhost:8080/api/v1/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "VALIDATED"}'
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "supplierId": 1,
  "orderDate": "2025-11-09T16:30:00Z",
  "status": "VALIDATED",
  "totalAmount": 17750.00,
  "items": [...]
}
```

#### Deliver Order (Creates Stock Movements)

**Request:**
```bash
curl -X PUT http://localhost:8080/api/v1/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "DELIVERED"}'
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "supplierId": 1,
  "orderDate": "2025-11-09T16:30:00Z",
  "status": "DELIVERED",
  "totalAmount": 17750.00,
  "items": [...]
}
```

**Note:** When status changes to `DELIVERED`, stock movements are created automatically for each order item.

#### Cancel Order

**Request:**
```bash
  curl -X PUT http://localhost:8080/api/v1/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "CANCELLED"}'
```

**Order Status Workflow:**
```
WAITING → VALIDATED → DELIVERED
   ↓
CANCELLED (from WAITING or VALIDATED only)
```

### Delete Order

**Endpoint:** `DELETE /api/v1/orders/{id}`

---

## Stock API

### Create Manual Stock Movement

**Endpoint:** `POST /api/v1/stock/movements`

#### Stock Entry

**Request:**
```bash
  curl -X POST http://localhost:8080/api/v1/stock/movements \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "type": "ENTRY",
    "quantity": 50,
    "unitCost": 150.00
  }'
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "productId": 1,
  "type": "ENTRY",
  "quantity": 50,
  "unitCost": 150.0,
  "totalCost": 7500.0,
  "remainingQuantity": 50.0,
  "orderId": null,
  "createdAt": "2025-11-09T16:45:00Z"
}
```

#### Stock Exit

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/stock/movements \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "type": "EXIT",
    "quantity": 20,
    "unitCost": 150.00
  }'
```

**Response:** `201 Created`
```json
{
  "id": 2,
  "productId": 1,
  "type": "EXIT",
  "quantity": 20,
  "unitCost": 150.0,
  "totalCost": 3000.0,
  "remainingQuantity": null,
  "orderId": null,
  "createdAt": "2025-11-09T16:50:00Z"
}
```

#### Stock Adjustment

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/stock/movements \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "type": "ADJUSTMENT",
    "quantity": 5,
    "unitCost": 150.00
  }'
```

**Movement Types:**
- `ENTRY` - Stock coming in (increases stock)
- `EXIT` - Stock going out (decreases stock)
- `ADJUSTMENT` - Manual correction (adjusts stock)

### Get All Stock Movements (Paginated)

**Endpoint:** `GET /api/v1/stock/movements?page=0&size=20`

**Request:**
```bash
curl http://localhost:8080/api/v1/stock/movements?page=0&size=20
```

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "productId": 1,
      "type": "ENTRY",
      "quantity": 100,
      "unitCost": 150.0,
      "totalCost": 15000.0,
      "remainingQuantity": 100.0,
      "orderId": 1,
      "createdAt": "2025-11-09T16:30:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1
}
```

### Get Product Movement History

**Endpoint:** `GET /api/v1/stock/products/{productId}/history`

**Request:**
```bash
curl http://localhost:8080/api/v1/stock/products/1/history
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "productId": 1,
    "type": "ENTRY",
    "quantity": 100,
    "unitCost": 150.0,
    "totalCost": 15000.0,
    "remainingQuantity": 80.0,
    "orderId": 1,
    "createdAt": "2025-11-09T16:30:00Z"
  },
  {
    "id": 2,
    "productId": 1,
    "type": "EXIT",
    "quantity": 20,
    "unitCost": 150.0,
    "totalCost": 3000.0,
    "remainingQuantity": null,
    "orderId": null,
    "createdAt": "2025-11-09T16:50:00Z"
  }
]
```

---

## Complete Workflow Example

### Step-by-Step: Order to Stock

```bash
# 1. Create Supplier
SUPPLIER=$(curl -s -X POST http://localhost:8080/api/v1/suppliers \
  -H "Content-Type: application/json" \
  -d '{"company": "Test Supplier", "email": "test@supplier.com", ...}')
SUPPLIER_ID=$(echo $SUPPLIER | jq -r '.id')

# 2. Create Product
PRODUCT=$(curl -s -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Test Product", "price": 100.0, "category": "TEXTILE", "supplierId": '$SUPPLIER_ID', ...}')
PRODUCT_ID=$(echo $PRODUCT | jq -r '.id')

# 3. Create Order
ORDER=$(curl -s -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{"supplierId": '$SUPPLIER_ID', "items": [{"productId": '$PRODUCT_ID', "quantity": 50, "unitPrice": 100.0}]}')
ORDER_ID=$(echo $ORDER | jq -r '.id')

# 4. Validate Order
curl -X PUT http://localhost:8080/api/v1/orders/$ORDER_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status": "VALIDATED"}'

# 5. Deliver Order (Creates Stock Movements)
curl -X PUT http://localhost:8080/api/v1/orders/$ORDER_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status": "DELIVERED"}'

# 6. Check Stock Movements
curl http://localhost:8080/api/v1/stock/movements

# 7. Check Product Stock
curl http://localhost:8080/api/v1/products/$PRODUCT_ID
# Should show: stockQuantity: 50, averageCost: 100.0
```

---

## Error Responses

### Insufficient Stock

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/stock/movements \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "type": "EXIT",
    "quantity": 10000,
    "unitCost": 150.00
  }'
```

**Response:** `400 Bad Request`
```json
{
  "code": "INSUFFICIENT_STOCK",
  "message": "Insufficient stock for product 1. Available: 100.00, Requested: 10000.00",
  "status": 400
}
```

### Invalid Status Transition

**Request:**
```bash
curl -X PUT http://localhost:8080/api/v1/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "WAITING"}'
```

**Response:** `400 Bad Request`
```json
{
  "code": "INTERNAL_ERROR",
  "message": "Cannot change status of a DELIVERED order",
  "status": 500
}
```

### Resource Not Found

**Response:** `404 Not Found`
```json
{
  "timestamp": "2025-11-09T16:00:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/api/v1/products/999"
}
```

---

## Pagination Parameters

All list endpoints support pagination:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | integer | 0 | Page number (0-indexed) |
| `size` | integer | 20 | Number of items per page |
| `sort` | string | - | Sort field and direction (e.g., `name,asc`) |

**Example:**
```bash
curl "http://localhost:8080/api/v1/products?page=0&size=10&sort=name,asc"
```

---

## Authentication

Currently, the API does not require authentication. In production, implement:
- JWT tokens
- OAuth2
- API keys

---

## Notes

- All timestamps are in ISO 8601 format with UTC timezone
- Prices and costs are in MAD (Moroccan Dirham)
- Stock quantities are integers
- Average costs are calculated automatically based on the configured valuation method (FIFO/CUMP)

---

**For more examples, see the test scripts in `/scripts/` directory.**
