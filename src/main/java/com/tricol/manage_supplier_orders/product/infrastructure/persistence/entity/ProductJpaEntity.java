package com.tricol.manage_supplier_orders.product.infrastructure.persistence.entity;

import com.tricol.manage_supplier_orders.product.domain.enums.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    private String name;
    private String description;
    private double price;
    private Category category;
    private String unit;
    private int stockQuantity;

    @Version
    private Long version;
}
