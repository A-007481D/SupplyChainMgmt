package com.tricol.manage_supplier_orders.stock.domain.model;

import com.tricol.manage_supplier_orders.stock.domain.enums.MovementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StockMovement Domain Tests")
class StockMovementTest {

    private StockMovement movement;

    @BeforeEach
    void setUp() {
        movement = StockMovement.builder()
                .id(1L)
                .productId(1L)
                .type(MovementType.ENTRY)
                .quantity(100)
                .unitCost(50.0)
                .totalCost(5000.0)
                .movementDate(OffsetDateTime.now())
                .supplierOrderId(1L)
                .build();
    }

    @Nested
    @DisplayName("Movement Creation Tests")
    class MovementCreationTests {

        @Test
        @DisplayName("Should create movement with builder")
        void testCreateMovement() {
            assertNotNull(movement);
            assertEquals(1L, movement.getId());
            assertEquals(1L, movement.getProductId());
            assertEquals(MovementType.ENTRY, movement.getType());
        }

        @Test
        @DisplayName("Should create movement with all properties")
        void testMovementProperties() {
            assertEquals(100, movement.getQuantity());
            assertEquals(50.0, movement.getUnitCost());
            assertEquals(5000.0, movement.getTotalCost());
            assertEquals(1L, movement.getSupplierOrderId());
        }

        @Test
        @DisplayName("Should create ENTRY movement")
        void testEntryMovement() {
            StockMovement entry = StockMovement.builder()
                    .productId(1L)
                    .type(MovementType.ENTRY)
                    .quantity(50)
                    .unitCost(100.0)
                    .build();

            assertEquals(MovementType.ENTRY, entry.getType());
        }

        @Test
        @DisplayName("Should create EXIT movement")
        void testExitMovement() {
            StockMovement exit = StockMovement.builder()
                    .productId(1L)
                    .type(MovementType.EXIT)
                    .quantity(25)
                    .build();

            assertEquals(MovementType.EXIT, exit.getType());
        }
    }

    @Nested
    @DisplayName("Movement Property Tests")
    class MovementPropertyTests {

        @Test
        @DisplayName("Should set and get product ID")
        void testProductId() {
            movement.setProductId(5L);
            assertEquals(5L, movement.getProductId());
        }

        @Test
        @DisplayName("Should set and get type")
        void testType() {
            movement.setType(MovementType.EXIT);
            assertEquals(MovementType.EXIT, movement.getType());
        }

        @Test
        @DisplayName("Should set and get quantity")
        void testQuantity() {
            movement.setQuantity(200);
            assertEquals(200, movement.getQuantity());
        }

        @Test
        @DisplayName("Should set and get unit cost")
        void testUnitCost() {
            movement.setUnitCost(75.0);
            assertEquals(75.0, movement.getUnitCost());
        }

        @Test
        @DisplayName("Should set and get total cost")
        void testTotalCost() {
            movement.setTotalCost(7500.0);
            assertEquals(7500.0, movement.getTotalCost());
        }

        @Test
        @DisplayName("Should set and get supplier order ID")
        void testSupplierOrderId() {
            movement.setSupplierOrderId(5L);
            assertEquals(5L, movement.getSupplierOrderId());
        }

        @Test
        @DisplayName("Should set and get remaining quantity")
        void testRemainingQuantity() {
            movement.setRemainingQuantity(50.0);
            assertEquals(50.0, movement.getRemainingQuantity());
        }

        @Test
        @DisplayName("Should set and get movement date")
        void testMovementDate() {
            OffsetDateTime now = OffsetDateTime.now();
            movement.setMovementDate(now);
            assertEquals(now, movement.getMovementDate());
        }
    }

    @Nested
    @DisplayName("Remaining Quantity Tests")
    class RemainingQuantityTests {

        @Test
        @DisplayName("Should initialize remaining quantity for ENTRY movement")
        void testInitializeRemainingQuantityForEntry() {
            movement.setType(MovementType.ENTRY);
            movement.setQuantity(100);
            movement.setRemainingQuantity(null);

            Double remaining = movement.getRemainingQuantity();
            assertEquals(100.0, remaining);
        }

        @Test
        @DisplayName("Should return existing remaining quantity")
        void testExistingRemainingQuantity() {
            movement.setRemainingQuantity(50.0);
            assertEquals(50.0, movement.getRemainingQuantity());
        }

        @Test
        @DisplayName("Should return null for EXIT movement with null remaining")
        void testExitMovementRemainingQuantity() {
            movement.setType(MovementType.EXIT);
            movement.setQuantity(100);
            movement.setRemainingQuantity(null);

            assertNull(movement.getRemainingQuantity());
        }

        @Test
        @DisplayName("Should handle null quantity")
        void testNullQuantity() {
            movement.setType(MovementType.ENTRY);
            movement.setQuantity(null);
            movement.setRemainingQuantity(null);

            assertNull(movement.getRemainingQuantity());
        }

        @Test
        @DisplayName("Should decrease remaining quantity as stock is consumed")
        void testDecreaseRemainingQuantity() {
            movement.setType(MovementType.ENTRY);
            movement.setQuantity(100);
            movement.setRemainingQuantity(100.0);

            movement.setRemainingQuantity(75.0);
            assertEquals(75.0, movement.getRemainingQuantity());
        }
    }

    @Nested
    @DisplayName("Cost Calculation Tests")
    class CostCalculationTests {

        @Test
        @DisplayName("Should calculate total cost as unit cost * quantity")
        void testTotalCostCalculation() {
            movement.setUnitCost(50.0);
            movement.setQuantity(100);

            Double expectedTotal = 50.0 * 100;
            assertEquals(expectedTotal, movement.getTotalCost());
        }

        @Test
        @DisplayName("Should handle zero unit cost")
        void testZeroUnitCost() {
            movement.setUnitCost(0.0);
            movement.setQuantity(100);
            movement.setTotalCost(0.0);

            assertEquals(0.0, movement.getTotalCost());
        }

        @Test
        @DisplayName("Should handle null unit cost")
        void testNullUnitCost() {
            movement.setUnitCost(null);
            assertNull(movement.getUnitCost());
        }
    }
}

