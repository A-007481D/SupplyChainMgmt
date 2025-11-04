package com.tricol.manage_supplier_orders.product.domain.model;

import com.tricol.manage_supplier_orders.product.domain.enums.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Category category;
    private String unit;
    private Integer stockQuantity;
    private Long supplierId;
    private Double averageCost;
}
