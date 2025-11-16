package com.tricol.manage_supplier_orders.order.domain.model;

import com.tricol.manage_supplier_orders.order.domain.enums.OrderStatus;
import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SupplierOrder Domain Tests")
class SupplierOrderTest {

    private SupplierOrder order;
    private Supplier supplier;

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder()
                .id(1L)
                .company("TechSupply Co")
                .address("123 Tech St")
                .contact("John Doe")
                .email("john@techsupply.com")
                .phone("555-1234")
                .city("San Francisco")
                .ice("ICE123456")
                .build();

        order = new SupplierOrder();
        order.setSupplier(supplier);
        order.setOrderDate(OffsetDateTime.now());
        order.setStatus(OrderStatus.WAITING);
        order.setItems(new ArrayList<>());
    }

    @Nested
    @DisplayName("Order Creation Tests")
    class OrderCreationTests {

        @Test
        @DisplayName("Should create order with basic details")
        void testCreateOrder() {
            assertNotNull(order);
            assertEquals(supplier, order.getSupplier());
            assertEquals(OrderStatus.WAITING, order.getStatus());
            assertNotNull(order.getOrderDate());
        }

        @Test
        @DisplayName("Should initialize items list as empty")
        void testInitializeItemsList() {
            assertTrue(order.getItems().isEmpty());
        }

        @Test
        @DisplayName("Should initialize total amount")
        void testInitializeTotalAmount() {
            order.recalcTotal();
            assertEquals(BigDecimal.ZERO, order.getTotalAmount());
        }
    }

    @Nested
    @DisplayName("Item Management Tests")
    class ItemManagementTests {

        @Test
        @DisplayName("Should add item to order")
        void testAddItem() {
            SupplierOrderItem item = new SupplierOrderItem();
            item.setProductId(1L);
            item.setQuantity(10);
            item.setUnitPrice(BigDecimal.valueOf(50.00));
            item.recalcSubtotal();

            order.addItem(item);

            assertEquals(1, order.getItems().size());
            assertEquals(item, order.getItems().get(0));
        }

        @Test
        @DisplayName("Should update total amount when adding item")
        void testTotalAmountAfterAddingItem() {
            SupplierOrderItem item = new SupplierOrderItem();
            item.setProductId(1L);
            item.setQuantity(10);
            item.setUnitPrice(BigDecimal.valueOf(50.00));
            item.recalcSubtotal();

            order.addItem(item);

            assertEquals(BigDecimal.valueOf(500.00), order.getTotalAmount());
        }

        @Test
        @DisplayName("Should add multiple items and calculate correct total")
        void testMultipleItems() {
            SupplierOrderItem item1 = new SupplierOrderItem(1L, 100L, 10, BigDecimal.valueOf(50.00));
            SupplierOrderItem item2 = new SupplierOrderItem(2L, 101L, 20, BigDecimal.valueOf(30.00));

            order.addItem(item1);
            order.addItem(item2);

            assertEquals(2, order.getItems().size());
            assertEquals(BigDecimal.valueOf(1100.00), order.getTotalAmount());
        }

        @Test
        @DisplayName("Should remove item from order")
        void testRemoveItem() {
            SupplierOrderItem item = new SupplierOrderItem(1L, 100L, 10, BigDecimal.valueOf(50.00));
            order.addItem(item);

            order.removeItem(item);

            assertTrue(order.getItems().isEmpty());
            assertEquals(BigDecimal.ZERO, order.getTotalAmount());
        }

        @Test
        @DisplayName("Should update total amount when removing item")
        void testTotalAmountAfterRemovingItem() {
            SupplierOrderItem item1 = new SupplierOrderItem(1L, 100L, 10, BigDecimal.valueOf(50.00));
            SupplierOrderItem item2 = new SupplierOrderItem(2L, 101L, 20, BigDecimal.valueOf(30.00));

            order.addItem(item1);
            order.addItem(item2);
            order.removeItem(item1);

            assertEquals(1, order.getItems().size());
            assertEquals(BigDecimal.valueOf(600.00), order.getTotalAmount());
        }
    }

    @Nested
    @DisplayName("Total Calculation Tests")
    class TotalCalculationTests {

        @Test
        @DisplayName("Should recalculate total amount")
        void testRecalcTotal() {
            SupplierOrderItem item1 = new SupplierOrderItem(1L, 100L, 5, BigDecimal.valueOf(100.00));
            SupplierOrderItem item2 = new SupplierOrderItem(2L, 101L, 3, BigDecimal.valueOf(200.00));

            order.addItem(item1);
            order.addItem(item2);

            order.recalcTotal();

            assertEquals(BigDecimal.valueOf(1100.00), order.getTotalAmount());
        }

        @Test
        @DisplayName("Should handle null unit prices in total calculation")
        void testRecalcTotalWithNullPrices() {
            SupplierOrderItem item1 = new SupplierOrderItem();
            item1.setProductId(1L);
            item1.setQuantity(10);
            item1.setUnitPrice(null);
            item1.recalcSubtotal();

            order.addItem(item1);

            assertEquals(BigDecimal.ZERO, order.getTotalAmount());
        }
    }
}

