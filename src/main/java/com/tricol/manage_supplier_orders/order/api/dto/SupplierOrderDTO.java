package com.tricol.manage_supplier_orders.order.api.dto;

import com.tricol.manage_supplier_orders.order.domain.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder @NoArgsConstructor
@AllArgsConstructor
public class SupplierOrderDTO {

        private Long id;
        private Long supplierId;
        private OffsetDateTime orderDate;
        private OrderStatus status;
        private BigDecimal totalAmount;
        private List<SupplierOrderItemDTO> items;
}
