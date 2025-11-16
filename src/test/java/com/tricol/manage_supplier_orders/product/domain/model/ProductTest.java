package com.tricol.manage_supplier_orders.product.domain.model;

import com.tricol.manage_supplier_orders.product.domain.enums.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Domain Tests")
class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("High-performance laptop");
        product.setPrice(1200.00);
        product.setCategory(Category.EQUIPMENT);
        product.setUnit("piece");
        product.setStockQuantity(50);
        product.setSupplierId(1L);
        product.setAverageCost(1000.00);
    }

    @Nested
    @DisplayName("Product Creation Tests")
    class ProductCreationTests {

        @Test
        @DisplayName("Should create product with all properties")
        void testCreateProduct() {
            assertNotNull(product);
            assertEquals(1L, product.getId());
            assertEquals("Laptop", product.getName());
            assertEquals("High-performance laptop", product.getDescription());
        }

        @Test
        @DisplayName("Should initialize product with null values")
        void testCreateProductWithDefaults() {
            Product newProduct = new Product();
            assertNull(newProduct.getId());
            assertNull(newProduct.getName());
            assertNull(newProduct.getPrice());
        }
    }

    @Nested
    @DisplayName("Product Property Tests")
    class ProductPropertyTests {

        @Test
        @DisplayName("Should set and get name")
        void testProductName() {
            product.setName("Desktop Computer");
            assertEquals("Desktop Computer", product.getName());
        }

        @Test
        @DisplayName("Should set and get description")
        void testProductDescription() {
            product.setDescription("Gaming desktop");
            assertEquals("Gaming desktop", product.getDescription());
        }

        @Test
        @DisplayName("Should set and get price")
        void testProductPrice() {
            product.setPrice(1500.00);
            assertEquals(1500.00, product.getPrice());
        }

        @Test
        @DisplayName("Should set and get category")
        void testProductCategory() {
            product.setCategory(Category.PACKAGING);
            assertEquals(Category.PACKAGING, product.getCategory());
        }

        @Test
        @DisplayName("Should set and get unit")
        void testProductUnit() {
            product.setUnit("box");
            assertEquals("box", product.getUnit());
        }

        @Test
        @DisplayName("Should set and get stock quantity")
        void testStockQuantity() {
            product.setStockQuantity(100);
            assertEquals(100, product.getStockQuantity());
        }

        @Test
        @DisplayName("Should set and get supplier ID")
        void testSupplierId() {
            product.setSupplierId(5L);
            assertEquals(5L, product.getSupplierId());
        }

        @Test
        @DisplayName("Should set and get average cost")
        void testAverageCost() {
            product.setAverageCost(950.00);
            assertEquals(950.00, product.getAverageCost());
        }
    }

    @Nested
    @DisplayName("Product Stock Management Tests")
    class ProductStockManagementTests {

        @Test
        @DisplayName("Should track stock quantity")
        void testStockTracking() {
            assertEquals(50, product.getStockQuantity());
        }

        @Test
        @DisplayName("Should allow zero stock")
        void testZeroStock() {
            product.setStockQuantity(0);
            assertEquals(0, product.getStockQuantity());
        }

        @Test
        @DisplayName("Should handle null stock quantity")
        void testNullStockQuantity() {
            product.setStockQuantity(null);
            assertNull(product.getStockQuantity());
        }

        @Test
        @DisplayName("Should handle negative stock (emergency case)")
        void testNegativeStock() {
            product.setStockQuantity(-10);
            assertEquals(-10, product.getStockQuantity());
        }
    }

    @Nested
    @DisplayName("Product Cost Tracking Tests")
    class ProductCostTrackingTests {

        @Test
        @DisplayName("Should track average cost")
        void testAverageCostTracking() {
            assertEquals(1000.00, product.getAverageCost());
        }

        @Test
        @DisplayName("Should handle null average cost")
        void testNullAverageCost() {
            product.setAverageCost(null);
            assertNull(product.getAverageCost());
        }

        @Test
        @DisplayName("Should allow zero average cost")
        void testZeroAverageCost() {
            product.setAverageCost(0.0);
            assertEquals(0.0, product.getAverageCost());
        }

        @Test
        @DisplayName("Should calculate profit margin (price - cost)")
        void testProfitMargin() {
            double profitMargin = product.getPrice() - product.getAverageCost();
            assertEquals(200.00, profitMargin);
        }
    }
}

