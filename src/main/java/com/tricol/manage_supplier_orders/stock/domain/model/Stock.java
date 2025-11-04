package com.tricol.manage_supplier_orders.stock.domain.model;

import com.tricol.manage_supplier_orders.product.domain.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Stock {
    private Long id;
    private Product product;
    private Integer quantity;

}
