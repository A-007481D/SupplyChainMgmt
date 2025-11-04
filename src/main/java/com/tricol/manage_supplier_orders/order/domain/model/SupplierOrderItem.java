package com.tricol.manage_supplier_orders.order.domain.model;

public class SupplierOrderItem {
    private Long productId;
    private Double unitPrice;
    private Integer quantity;


    public Double getLineTotal() {
        return unitPrice * quantity;
    }
}
