package com.tricol.manage_supplier_orders.order.application.service;

import com.tricol.manage_supplier_orders.order.application.ports.*;
import com.tricol.manage_supplier_orders.order.domain.enums.OrderStatus;
import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrder;
import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrderItem;
import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SupplierOrderService Unit Tests")
class SupplierOrderServiceTest {

    @Mock
    private SupplierOrderRepository repository;

    @Mock
    private ProductPort productPort;

    @Mock
    private SupplierPort supplierPort;

    @Mock
    private StockPort stockPort;

    @InjectMocks
    private SupplierOrderService supplierOrderService;

    private SupplierOrder order;
    private Supplier supplier;
    private SupplierOrderItem item;

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder()
                .id(1L)
                .company("TechSupply Inc")
                .address("123 Tech St")
                .contact("John")
                .email("john@tech.com")
                .phone("555-1234")
                .city("SF")
                .ice("ICE123")
                .build();

        item = new SupplierOrderItem(1L, 100L, 10, BigDecimal.valueOf(50.00));

        order = new SupplierOrder();
        order.setId(1L);
        order.setSupplier(supplier);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDate(OffsetDateTime.now());
        order.setItems(new ArrayList<>());
        order.addItem(item);
    }

    @Nested
    @DisplayName("Create Order Tests")
    class CreateOrderTests {

        @Test
        @DisplayName("Should create order successfully")
        void testCreateOrder() {
            when(supplierPort.existsById(1L)).thenReturn(Optional.of(true));
            when(repository.save(any(SupplierOrder.class))).thenReturn(order);

            SupplierOrder result = supplierOrderService.createOrder(order);

            assertNotNull(result);
            assertEquals(OrderStatus.WAITING, result.getStatus());
            verify(repository, times(1)).save(any(SupplierOrder.class));
        }

        @Test
        @DisplayName("Should throw exception when supplier does not exist")
        void testCreateOrderSupplierNotFound() {
            when(supplierPort.existsById(1L)).thenReturn(Optional.of(false));

            assertThrows(IllegalArgumentException.class, () -> {
                supplierOrderService.createOrder(order);
            });
        }

        @Test
        @DisplayName("Should throw exception when supplier is null")
        void testCreateOrderSupplierNull() {
            order.setSupplier(null);

            assertThrows(IllegalArgumentException.class, () -> {
                supplierOrderService.createOrder(order);
            });
        }

        @Test
        @DisplayName("Should throw exception when order has no items")
        void testCreateOrderNoItems() {
            when(supplierPort.existsById(1L)).thenReturn(Optional.of(true));
            order.setItems(new ArrayList<>());

            assertThrows(IllegalArgumentException.class, () -> {
                supplierOrderService.createOrder(order);
            });
        }

        @Test
        @DisplayName("Should set default status when not provided")
        void testCreateOrderDefaultStatus() {
            order.setStatus(null);
            when(supplierPort.existsById(1L)).thenReturn(Optional.of(true));
            when(repository.save(any(SupplierOrder.class))).thenReturn(order);

            supplierOrderService.createOrder(order);

            verify(repository, times(1)).save(any(SupplierOrder.class));
        }

        @Test
        @DisplayName("Should validate item quantity")
        void testCreateOrderInvalidItemQuantity() {
            when(supplierPort.existsById(1L)).thenReturn(Optional.of(true));
            SupplierOrderItem invalidItem = new SupplierOrderItem(1L, 100L, 0, BigDecimal.valueOf(50.00));
            order.setItems(new ArrayList<>(List.of(invalidItem)));

            assertThrows(IllegalArgumentException.class, () -> {
                supplierOrderService.createOrder(order);
            });
        }

        @Test
        @DisplayName("Should fetch product price if not provided")
        void testCreateOrderFetchProductPrice() {
            when(supplierPort.existsById(1L)).thenReturn(Optional.of(true));
            SupplierOrderItem itemNoPrice = new SupplierOrderItem(1L, 100L, 10, null);
            order.setItems(new ArrayList<>(List.of(itemNoPrice)));
            when(productPort.getPriceById(100L)).thenReturn(Optional.of(BigDecimal.valueOf(75.00)));
            when(repository.save(any(SupplierOrder.class))).thenReturn(order);

            supplierOrderService.createOrder(order);

            verify(productPort, times(1)).getPriceById(100L);
        }
    }

    @Nested
    @DisplayName("Get Order Tests")
    class GetOrderTests {

        @Test
        @DisplayName("Should get order by ID successfully")
        void testGetOrderById() {
            when(repository.findById(1L)).thenReturn(Optional.of(order));

            SupplierOrder result = supplierOrderService.getById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            verify(repository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when order not found")
        void testGetOrderByIdNotFound() {
            when(repository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> {
                supplierOrderService.getById(999L);
            });
        }
    }

    @Nested
    @DisplayName("Update Order Status Tests")
    class UpdateOrderStatusTests {

        @Test
        @DisplayName("Should update status from WAITING to VALIDATED")
        void testUpdateStatusWaitingToValidated() {
            order.setStatus(OrderStatus.WAITING);
            when(repository.findById(1L)).thenReturn(Optional.of(order));
            when(repository.save(any(SupplierOrder.class))).thenReturn(order);

            supplierOrderService.updateStatus(1L, OrderStatus.VALIDATED);

            verify(repository, times(1)).save(any(SupplierOrder.class));
        }

        @Test
        @DisplayName("Should update status from VALIDATED to DELIVERED")
        void testUpdateStatusValidatedToDelivered() {
            order.setStatus(OrderStatus.VALIDATED);
            when(repository.findById(1L)).thenReturn(Optional.of(order));
            when(repository.save(any(SupplierOrder.class))).thenReturn(order);
            doNothing().when(stockPort).recordEntry(anyLong(), anyInt(), anyLong(), any(BigDecimal.class));

            supplierOrderService.updateStatus(1L, OrderStatus.DELIVERED);

            verify(stockPort, times(order.getItems().size())).recordEntry(anyLong(), anyInt(), anyLong(), any(BigDecimal.class));
        }

        @Test
        @DisplayName("Should throw exception when trying to deliver non-VALIDATED order")
        void testUpdateStatusDeliverInvalidState() {
            order.setStatus(OrderStatus.WAITING);
            when(repository.findById(1L)).thenReturn(Optional.of(order));

            assertThrows(IllegalStateException.class, () -> {
                supplierOrderService.updateStatus(1L, OrderStatus.DELIVERED);
            });
        }

        @Test
        @DisplayName("Should throw exception when trying to modify DELIVERED order")
        void testUpdateStatusDeliveredOrder() {
            order.setStatus(OrderStatus.DELIVERED);
            when(repository.findById(1L)).thenReturn(Optional.of(order));

            assertThrows(IllegalStateException.class, () -> {
                supplierOrderService.updateStatus(1L, OrderStatus.VALIDATED);
            });
        }

        @Test
        @DisplayName("Should cancel order from WAITING status")
        void testCancelOrderFromWaiting() {
            order.setStatus(OrderStatus.WAITING);
            when(repository.findById(1L)).thenReturn(Optional.of(order));
            when(repository.save(any(SupplierOrder.class))).thenReturn(order);

            supplierOrderService.updateStatus(1L, OrderStatus.CANCELLED);

            verify(repository, times(1)).save(any(SupplierOrder.class));
        }
    }

    @Nested
    @DisplayName("List Orders Tests")
    class ListOrdersTests {

        @Test
        @DisplayName("Should list orders with pagination")
        void testListOrders() {
            Page<SupplierOrder> page = new PageImpl<>(new ArrayList<>());
            PageRequest pageable = PageRequest.of(0, 10);

            when(repository.findAll(pageable)).thenReturn(page);

            Page<SupplierOrder> result = supplierOrderService.list(pageable);

            assertNotNull(result);
            verify(repository, times(1)).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("Delete Order Tests")
    class DeleteOrderTests {

        @Test
        @DisplayName("Should delete order successfully")
        void testDeleteOrder() {
            doNothing().when(repository).deleteById(1L);

            supplierOrderService.delete(1L);

            verify(repository, times(1)).deleteById(1L);
        }
    }
}

