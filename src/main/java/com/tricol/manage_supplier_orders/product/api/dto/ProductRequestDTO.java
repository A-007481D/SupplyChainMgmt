package com.tricol.manage_supplier_orders.product.api.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequestDTO {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @DecimalMin(value = "0.0" , inclusive = false)
    private Double price;
    private String category;
    private String unit;
    private int stockQuantity;
    private Long supplierId;
}
