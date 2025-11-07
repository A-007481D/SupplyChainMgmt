package com.tricol.manage_supplier_orders.stock.infrastructure.persistence.entity;


import com.tricol.manage_supplier_orders.stock.domain.enums.MovementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.OffsetDateTime;


@Entity
@Table(name = "stock_movement")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "product_id", nullable = false)
    private Long productId;


    @Enumerated(EnumType.STRING)
    private MovementType type;


    private Integer quantity;


    @Column(name = "unit_cost")
    private Double unitCost;


    @Column(name = "total_cost")
    private Double totalCost;


    @Column(name = "movement_date")
    private OffsetDateTime movementDate;


    @Column(name = "supplier_order_id")
    private Long supplierOrderId;
}