package com.tricol.manage_supplier_orders.stock.domain.model;


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
public class StockMovement {
    private Long id;
    private Long productId;
    private MovementType type;
    private Integer quantity;
    private Double unitCost; // cost per unit at entry
    private Double totalCost; // unitCost * quantity
    private OffsetDateTime movementDate;
    private Long supplierOrderId; // optional
}