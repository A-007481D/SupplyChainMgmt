package com.tricol.manage_supplier_orders.order.infrastructure.persistence.entity;

import com.tricol.manage_supplier_orders.order.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "supplier_order")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SupplierOrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long supplierId;

    private OffsetDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "supplierOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplierOrderItemJpaEntity> items = new ArrayList<>();
}
