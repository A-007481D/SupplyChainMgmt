package com.tricol.manage_supplier_orders.stock.api.dto;

import com.tricol.manage_supplier_orders.stock.domain.enums.MovementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStockMovementRequest {
    @NotNull
    private Long productId;

    @NotNull
    private MovementType type;

    @NotNull
    @Min(1)
    private Integer quantity;

    // optional: cost per unit for this movement (for entries)
    private Double unitCost;

    private Long orderId;
}
