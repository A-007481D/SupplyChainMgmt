package com.tricol.manage_supplier_orders.stock.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntryItemDTO {
    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    private Integer quantity;

    // optional, if not provided service will use product price or fallback
    private Double unitCost;
}
