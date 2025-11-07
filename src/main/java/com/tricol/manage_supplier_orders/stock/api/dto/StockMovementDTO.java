package com.tricol.manage_supplier_orders.stock.api.dto;

import com.tricol.manage_supplier_orders.stock.domain.enums.MovementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementDTO {
    private Long id;
    private Long productId;
    private MovementType type;
    private Integer quantity;
    private Double unitCost;
    private Long orderId;
    private OffsetDateTime createdAt;
}
