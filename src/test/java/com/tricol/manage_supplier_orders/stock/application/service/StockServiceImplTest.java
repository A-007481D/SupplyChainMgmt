package com.tricol.manage_supplier_orders.stock.application.service;

import com.tricol.manage_supplier_orders.product.domain.model.Product;
import com.tricol.manage_supplier_orders.product.domain.ports.ProductRepositoryPort;
import com.tricol.manage_supplier_orders.stock.domain.enums.MovementType;
import com.tricol.manage_supplier_orders.stock.domain.enums.StockValuationMethod;
import com.tricol.manage_supplier_orders.stock.domain.exception.InsufficientStockException;
import com.tricol.manage_supplier_orders.stock.domain.model.StockMovement;
import com.tricol.manage_supplier_orders.stock.domain.ports.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StockService Unit Tests")
class StockServiceImplTest {

    @Mock
    private StockMovementRepository stockRepository;

    @Mock
    private ProductRepositoryPort productRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    private Product product;
    private StockMovement movement;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setStockQuantity(100);
        product.setAverageCost(1000.0);

        movement = StockMovement.builder()
                .id(1L)
                .productId(1L)
                .type(MovementType.ENTRY)
                .quantity(50)
                .unitCost(100.0)
                .totalCost(5000.0)
                .movementDate(OffsetDateTime.now())
                .supplierOrderId(1L)
                .build();

        // Set FIFO as default valuation method
        ReflectionTestUtils.setField(stockService, "valuationMethod", StockValuationMethod.FIFO);
    }

    @Nested
    @DisplayName("Record Movement Tests")
    class RecordMovementTests {

        @Test
        @DisplayName("Should record entry movement successfully")
        void testRecordEntryMovement() {
            when(productRepository.findById(1L))
                    .thenReturn(Optional.of(product));
            when(stockRepository.save(any(StockMovement.class)))
                    .thenReturn(movement);
            when(productRepository.save(any(Product.class)))
                    .thenReturn(product);

            StockMovement result = stockService.recordMovement(
                    1L, MovementType.ENTRY, 50, 100.0, 1L
            );

            assertNotNull(result);
            assertEquals(MovementType.ENTRY, result.getType());
            verify(productRepository, times(1)).save(any(Product.class));
            verify(stockRepository, times(1)).save(any(StockMovement.class));
        }

        @Test
        @DisplayName("Should throw exception for non-existent product")
        void testRecordMovementProductNotFound() {
            when(productRepository.findById(999L))
                    .thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> {
                stockService.recordMovement(999L, MovementType.ENTRY, 50, 100.0, 1L);
            });
        }

        @Test
        @DisplayName("Should record exit movement when stock is sufficient")
        void testRecordExitMovement() {
            movement.setType(MovementType.EXIT);
            product.setStockQuantity(100);

            when(productRepository.findById(1L))
                    .thenReturn(Optional.of(product));
            when(stockRepository.save(any(StockMovement.class)))
                    .thenReturn(movement);
            when(productRepository.save(any(Product.class)))
                    .thenReturn(product);

            StockMovement result = stockService.recordMovement(
                    1L, MovementType.EXIT, 50, null, null
            );

            assertNotNull(result);
            assertEquals(MovementType.EXIT, result.getType());
        }

        @Test
        @DisplayName("Should throw exception for insufficient stock on exit")
        void testRecordExitMovementInsufficientStock() {
            product.setStockQuantity(10);
            movement.setType(MovementType.EXIT);

            when(productRepository.findById(1L))
                    .thenReturn(Optional.of(product));

            assertThrows(InsufficientStockException.class, () -> {
                stockService.recordMovement(1L, MovementType.EXIT, 50, null, null);
            });
        }

        @Test
        @DisplayName("Should update product stock quantity after entry")
        void testUpdateStockQuantityAfterEntry() {
            int initialStock = 100;
            int entryQty = 50;
            product.setStockQuantity(initialStock);

            when(productRepository.findById(1L))
                    .thenReturn(Optional.of(product));
            when(stockRepository.save(any(StockMovement.class)))
                    .thenReturn(movement);
            when(productRepository.save(any(Product.class)))
                    .thenReturn(product);

            stockService.recordMovement(1L, MovementType.ENTRY, entryQty, 100.0, 1L);

            verify(productRepository, times(1)).save(argThat(p ->
                    p.getId().equals(1L) && p.getStockQuantity() == initialStock + entryQty
            ));
        }
    }

    @Nested
    @DisplayName("List Movements Tests")
    class ListMovementsTests {

        @Test
        @DisplayName("Should list all movements with pagination")
        void testListMovements() {
            Page<StockMovement> page = new PageImpl<>(new ArrayList<>());
            PageRequest pageable = PageRequest.of(0, 10);

            when(stockRepository.findAll(pageable))
                    .thenReturn(page);

            Page<StockMovement> result = stockService.listMovements(pageable);

            assertNotNull(result);
            verify(stockRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Should list movements by product")
        void testListMovementsByProduct() {
            Page<StockMovement> page = new PageImpl<>(new ArrayList<>());
            PageRequest pageable = PageRequest.of(0, 10);

            when(stockRepository.findByProductId(1L, pageable))
                    .thenReturn(page);

            Page<StockMovement> result = stockService.listByProduct(1L, pageable);

            assertNotNull(result);
            verify(stockRepository, times(1)).findByProductId(1L, pageable);
        }

        @Test
        @DisplayName("Should list movements by supplier order")
        void testListMovementsBySupplierOrder() {
            Page<StockMovement> page = new PageImpl<>(new ArrayList<>());
            PageRequest pageable = PageRequest.of(0, 10);

            when(stockRepository.findBySupplierOrderId(1L, pageable))
                    .thenReturn(page);

            Page<StockMovement> result = stockService.listBySupplierOrder(1L, pageable);

            assertNotNull(result);
            verify(stockRepository, times(1)).findBySupplierOrderId(1L, pageable);
        }
    }

    @Nested
    @DisplayName("Handle Order Delivery Tests")
    class HandleOrderDeliveryTests {

        @Test
        @DisplayName("Should handle order delivery with multiple movements")
        void testHandleOrderDelivery() {
            List<StockMovement> movements = new ArrayList<>(List.of(movement));

            when(productRepository.findById(1L))
                    .thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class)))
                    .thenReturn(product);
            when(stockRepository.save(any(StockMovement.class)))
                    .thenReturn(movement);

            stockService.handleOrderDelivery(1L, movements);

            verify(productRepository, times(1)).findById(1L);
            verify(productRepository, times(1)).save(any(Product.class));
            verify(stockRepository, times(1)).save(any(StockMovement.class));
        }

        @Test
        @DisplayName("Should throw exception for non-existent product in delivery")
        void testHandleOrderDeliveryProductNotFound() {
            List<StockMovement> movements = new ArrayList<>(List.of(movement));

            when(productRepository.findById(1L))
                    .thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> {
                stockService.handleOrderDelivery(1L, movements);
            });
        }
    }

    @Nested
    @DisplayName("FIFO Calculation Tests")
    class FIFOCalculationTests {

        @Test
        @DisplayName("Should initialize remaining quantity for ENTRY with FIFO method")
        void testInitializeRemainingQuantityFIFO() {
            ReflectionTestUtils.setField(stockService, "valuationMethod", StockValuationMethod.FIFO);

            when(productRepository.findById(1L))
                    .thenReturn(Optional.of(product));
            when(stockRepository.save(any(StockMovement.class)))
                    .thenReturn(movement);
            when(productRepository.save(any(Product.class)))
                    .thenReturn(product);

            StockMovement result = stockService.recordMovement(1L, MovementType.ENTRY, 50, 100.0, 1L);

            assertNotNull(result.getRemainingQuantity());
            assertEquals(50.0, result.getRemainingQuantity());
        }
    }

    @Nested
    @DisplayName("CUMP Calculation Tests")
    class CUMPCalculationTests {

        @Test
        @DisplayName("Should calculate CUMP cost on entry")
        void testCalculateCumpCostOnEntry() {
            ReflectionTestUtils.setField(stockService, "valuationMethod", StockValuationMethod.CUMP);

            product.setAverageCost(1000.0);
            product.setStockQuantity(100);

            when(productRepository.findById(1L))
                    .thenReturn(Optional.of(product));
            when(stockRepository.save(any(StockMovement.class)))
                    .thenReturn(movement);
            when(productRepository.save(any(Product.class)))
                    .thenReturn(product);

            StockMovement result = stockService.recordMovement(1L, MovementType.ENTRY, 50, 1200.0, 1L);

            assertNotNull(result);
            verify(productRepository, times(1)).save(any(Product.class));
        }
    }
}

