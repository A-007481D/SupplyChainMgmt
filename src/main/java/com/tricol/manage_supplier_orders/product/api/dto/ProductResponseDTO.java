package com.tricol.manage_supplier_orders.product.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private String unit;
    private Integer stockQuantity;
    private Long supplierId;
    private Double averageCost;
}
