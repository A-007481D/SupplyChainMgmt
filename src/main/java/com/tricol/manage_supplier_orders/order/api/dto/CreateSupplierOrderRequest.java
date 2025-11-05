package com.tricol.manage_supplier_orders.order.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateSupplierOrderRequest {

    @NotNull
    private Long supplierId;

    @NotEmpty
    @Valid
    private List<SupplierOrderItemDTO> items;
}
