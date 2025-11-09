package com.tricol.manage_supplier_orders.order.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSupplierOrderRequest {

    @NotNull(message = "Supplier ID is required")
    @Positive(message = "Supplier ID must be a positive number")
    private Long supplierId;

    @Valid
    @NotEmpty(message = "At least one order item is required")
    @Size(min = 1, message = "At least one order item is required")
    private List<@Valid CreateSupplierOrderItemRequest> items = new ArrayList<>();
}
