package com.tricol.manage_supplier_orders.order.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierOrderItem {
    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public SupplierOrderItem(Long id, Long productId, Integer quantity, BigDecimal unitPrice) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        recalcSubtotal();
    }

    public void recalcSubtotal() {
        if (unitPrice == null || quantity == null) {
            this.subtotal = BigDecimal.ZERO;
        } else {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

}
