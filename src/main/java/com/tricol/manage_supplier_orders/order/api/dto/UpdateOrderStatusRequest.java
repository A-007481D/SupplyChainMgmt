package com.tricol.manage_supplier_orders.order.api.dto;

import com.tricol.manage_supplier_orders.order.domain.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    @NotNull
    private OrderStatus status;
}
