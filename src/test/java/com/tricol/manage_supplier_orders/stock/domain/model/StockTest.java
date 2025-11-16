package com.tricol.manage_supplier_orders.stock.domain.model;

import com.tricol.manage_supplier_orders.product.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Stock Domain Tests")
class StockTest {

    private Stock stock;
    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(1200.00);
        product.setStockQuantity(50);

        stock = new Stock();
        stock.setId(1L);
        stock.setProduct(product);
        stock.setQuantity(50);
    }

    @Nested
    @DisplayName("Stock Creation Tests")
    class StockCreationTests {

        @Test
        @DisplayName("Should create stock with all properties")
        void testCreateStock() {
            assertNotNull(stock);
            assertEquals(1L, stock.getId());
            assertEquals(product, stock.getProduct());
            assertEquals(50, stock.getQuantity());
        }

        @Test
        @DisplayName("Should initialize stock with default constructor")
        void testCreateStockWithDefaults() {
            Stock newStock = new Stock();
            assertNull(newStock.getId());
            assertNull(newStock.getProduct());
            assertNull(newStock.getQuantity());
        }
    }

    @Nested
    @DisplayName("Stock Property Tests")
    class StockPropertyTests {

        @Test
        @DisplayName("Should set and get product")
        void testProduct() {
            Product newProduct = new Product();
            newProduct.setId(2L);
            stock.setProduct(newProduct);
            assertEquals(newProduct, stock.getProduct());
        }

        @Test
        @DisplayName("Should set and get quantity")
        void testQuantity() {
            stock.setQuantity(100);
            assertEquals(100, stock.getQuantity());
        }

        @Test
        @DisplayName("Should set and get id")
        void testId() {
            stock.setId(5L);
            assertEquals(5L, stock.getId());
        }
    }

    @Nested
    @DisplayName("Stock Quantity Management Tests")
    class StockQuantityManagementTests {

        @Test
        @DisplayName("Should allow zero quantity")
        void testZeroQuantity() {
            stock.setQuantity(0);
            assertEquals(0, stock.getQuantity());
        }

        @Test
        @DisplayName("Should allow null quantity")
        void testNullQuantity() {
            stock.setQuantity(null);
            assertNull(stock.getQuantity());
        }

        @Test
        @DisplayName("Should track large quantities")
        void testLargeQuantities() {
            stock.setQuantity(1000000);
            assertEquals(1000000, stock.getQuantity());
        }
    }
}

