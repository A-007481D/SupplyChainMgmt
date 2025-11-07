package com.tricol.manage_supplier_orders.stock.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDeliveryRequest {
    @NotNull
    private Long orderId;

    // valuationMethod: "CUMP" or "FIFO"
    private String valuationMethod = "CUMP";

    private List<OrderEntryItemDTO> items;
}
