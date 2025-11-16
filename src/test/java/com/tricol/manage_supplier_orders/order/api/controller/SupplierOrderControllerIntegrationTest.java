package com.tricol.manage_supplier_orders.order.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tricol.manage_supplier_orders.order.api.dto.CreateSupplierOrderRequest;
import com.tricol.manage_supplier_orders.order.api.dto.CreateSupplierOrderItemRequest;
import com.tricol.manage_supplier_orders.order.api.dto.UpdateOrderStatusRequest;
import com.tricol.manage_supplier_orders.order.application.service.SupplierOrderService;
import com.tricol.manage_supplier_orders.order.domain.enums.OrderStatus;
import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrder;
import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrderItem;
import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("SupplierOrderController Integration Tests")
class SupplierOrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SupplierOrderService supplierOrderService;

    private SupplierOrder order;
    private CreateSupplierOrderRequest requestDTO;

    @BeforeEach
    void setUp() {
        Supplier supplier = Supplier.builder()
                .id(1L)
                .company("TechSupply Inc")
                .address("123 Tech St")
                .contact("John")
                .email("john@tech.com")
                .phone("555-1234")
                .city("SF")
                .ice("ICE123")
                .build();

        SupplierOrderItem item = new SupplierOrderItem(1L, 100L, 10, BigDecimal.valueOf(50.00));

        order = new SupplierOrder();
        order.setId(1L);
        order.setSupplier(supplier);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDate(OffsetDateTime.now());
        order.setItems(new ArrayList<>(List.of(item)));
        order.recalcTotal();

        CreateSupplierOrderItemRequest itemReq = new CreateSupplierOrderItemRequest();
        itemReq.setProductId(100L);
        itemReq.setQuantity(10);
        itemReq.setUnitPrice(BigDecimal.valueOf(50.00));

        requestDTO = new CreateSupplierOrderRequest();
        requestDTO.setSupplierId(1L);
        requestDTO.setItems(new ArrayList<>(List.of(itemReq)));
    }

    @Nested
    @DisplayName("Create Order API Tests")
    class CreateOrderTests {

        @Test
        @DisplayName("Should create order with valid request")
        void testCreateOrder() throws Exception {
            when(supplierOrderService.createOrder(any(SupplierOrder.class)))
                    .thenReturn(order);

            mockMvc.perform(post("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.status").value("WAITING"));
        }

        @Test
        @DisplayName("Should return 400 for invalid request (no supplier)")
        void testCreateOrderNoSupplier() throws Exception {
            CreateSupplierOrderRequest invalidReq = new CreateSupplierOrderRequest();
            invalidReq.setSupplierId(null);
            invalidReq.setItems(new ArrayList<>());

            mockMvc.perform(post("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidReq)))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Get Order API Tests")
    class GetOrderTests {

        @Test
        @DisplayName("Should get order by ID")
        void testGetOrder() throws Exception {
            when(supplierOrderService.getById(1L))
                    .thenReturn(order);

            mockMvc.perform(get("/api/v1/orders/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));
        }

        @Test
        @DisplayName("Should return 404 for non-existent order")
        void testGetOrderNotFound() throws Exception {
            when(supplierOrderService.getById(999L))
                    .thenThrow(new IllegalArgumentException("Order not found"));

            mockMvc.perform(get("/api/v1/orders/999"))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Update Order Status API Tests")
    class UpdateOrderStatusTests {

        @Test
        @DisplayName("Should update order status")
        void testUpdateOrderStatus() throws Exception {
            order.setStatus(OrderStatus.VALIDATED);

            when(supplierOrderService.updateStatus(1L, OrderStatus.VALIDATED))
                    .thenReturn(order);

            UpdateOrderStatusRequest statusRequest = new UpdateOrderStatusRequest();
            statusRequest.setStatus(OrderStatus.VALIDATED);

            mockMvc.perform(put("/api/v1/orders/1/status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(statusRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("VALIDATED"));
        }

        @Test
        @DisplayName("Should return error for invalid status transition")
        void testUpdateOrderStatusInvalidTransition() throws Exception {
            order.setStatus(OrderStatus.DELIVERED);

            when(supplierOrderService.updateStatus(1L, OrderStatus.WAITING))
                    .thenThrow(new IllegalStateException("Cannot change status of a DELIVERED order"));

            UpdateOrderStatusRequest statusRequest = new UpdateOrderStatusRequest();
            statusRequest.setStatus(OrderStatus.WAITING);

            mockMvc.perform(put("/api/v1/orders/1/status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(statusRequest)))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("List Orders API Tests")
    class ListOrdersTests {

        @Test
        @DisplayName("Should list all orders with pagination")
        void testListOrders() throws Exception {
            Page<SupplierOrder> page = new PageImpl<>(new ArrayList<>(List.of(order)));

            when(supplierOrderService.list(any()))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }
    }

    @Nested
    @DisplayName("Delete Order API Tests")
    class DeleteOrderTests {

        @Test
        @DisplayName("Should delete order")
        void testDeleteOrder() throws Exception {
            mockMvc.perform(delete("/api/v1/orders/1"))
                    .andExpect(status().isNoContent());
        }
    }
}

