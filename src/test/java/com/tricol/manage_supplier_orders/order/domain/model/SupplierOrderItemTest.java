package com.tricol.manage_supplier_orders.order.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SupplierOrderItem Domain Tests")
class SupplierOrderItemTest {

    private SupplierOrderItem item;

    @BeforeEach
    void setUp() {
        item = new SupplierOrderItem();
        item.setProductId(1L);
    }

    @Nested
    @DisplayName("Item Creation Tests")
    class ItemCreationTests {

        @Test
        @DisplayName("Should create item with default constructor")
        void testCreateItemWithDefaultConstructor() {
            assertNotNull(item);
            assertEquals(1L, item.getProductId());
        }

        @Test
        @DisplayName("Should create item with all parameters")
        void testCreateItemWithAllParameters() {
            SupplierOrderItem newItem = new SupplierOrderItem(
                    1L, 100L, 10, BigDecimal.valueOf(50.00)
            );

            assertEquals(1L, newItem.getId());
            assertEquals(100L, newItem.getProductId());
            assertEquals(10, newItem.getQuantity());
            assertEquals(BigDecimal.valueOf(50.00), newItem.getUnitPrice());
            assertEquals(BigDecimal.valueOf(500.00), newItem.getSubtotal());
        }
    }

    @Nested
    @DisplayName("Subtotal Calculation Tests")
    class SubtotalCalculationTests {

        @Test
        @DisplayName("Should calculate subtotal correctly")
        void testCalculateSubtotal() {
            item.setQuantity(10);
            item.setUnitPrice(BigDecimal.valueOf(50.00));
            item.recalcSubtotal();

            assertEquals(BigDecimal.valueOf(500.00), item.getSubtotal());
        }

        @Test
        @DisplayName("Should handle zero quantity")
        void testZeroQuantity() {
            item.setQuantity(0);
            item.setUnitPrice(BigDecimal.valueOf(50.00));
            item.recalcSubtotal();

            assertEquals(0, item.getSubtotal().compareTo(BigDecimal.ZERO),
                         "Subtotal should be zero when quantity is zero");
        }

        @Test
        @DisplayName("Should handle null unit price")
        void testNullUnitPrice() {
            item.setQuantity(10);
            item.setUnitPrice(null);
            item.recalcSubtotal();

            assertEquals(BigDecimal.ZERO, item.getSubtotal());
        }

        @Test
        @DisplayName("Should handle null quantity")
        void testNullQuantity() {
            item.setQuantity(null);
            item.setUnitPrice(BigDecimal.valueOf(50.00));
            item.recalcSubtotal();

            assertEquals(BigDecimal.ZERO, item.getSubtotal());
        }

        @Test
        @DisplayName("Should handle both quantity and unit price as null")
        void testBothNull() {
            item.setQuantity(null);
            item.setUnitPrice(null);
            item.recalcSubtotal();

            assertEquals(BigDecimal.ZERO, item.getSubtotal());
        }

        @Test
        @DisplayName("Should handle decimal quantities and prices")
        void testDecimalCalculation() {
            item.setQuantity(15);
            item.setUnitPrice(BigDecimal.valueOf(99.99));
            item.recalcSubtotal();

            assertEquals(BigDecimal.valueOf(1499.85), item.getSubtotal());
        }
    }

    @Nested
    @DisplayName("Item Property Tests")
    class ItemPropertyTests {

        @Test
        @DisplayName("Should set and get product ID")
        void testProductId() {
            item.setProductId(100L);
            assertEquals(100L, item.getProductId());
        }

        @Test
        @DisplayName("Should set and get quantity")
        void testQuantity() {
            item.setQuantity(50);
            assertEquals(50, item.getQuantity());
        }

        @Test
        @DisplayName("Should set and get unit price")
        void testUnitPrice() {
            BigDecimal price = BigDecimal.valueOf(75.50);
            item.setUnitPrice(price);
            assertEquals(price, item.getUnitPrice());
        }

        @Test
        @DisplayName("Should set and get subtotal")
        void testSubtotal() {
            BigDecimal subtotal = BigDecimal.valueOf(1000.00);
            item.setSubtotal(subtotal);
            assertEquals(subtotal, item.getSubtotal());
        }

        @Test
        @DisplayName("Should set and get order reference")
        void testOrderReference() {
            SupplierOrder order = new SupplierOrder();
            item.setOrder(order);
            assertEquals(order, item.getOrder());
        }
    }
}

