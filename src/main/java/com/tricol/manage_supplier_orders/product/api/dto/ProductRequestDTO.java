package com.tricol.manage_supplier_orders.product.api.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequestDTO {
    private String name;
    private String description;
    private double price;
    private String category;
    private String unit;
    private int stockQuantity;
    private Long supplierId;
}
