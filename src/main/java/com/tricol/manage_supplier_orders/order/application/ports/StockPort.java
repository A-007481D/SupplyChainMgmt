package com.tricol.manage_supplier_orders.order.application.ports;

public interface StockPort {

    /**
     * Record an entry movement for productId and quantity, associated to supplierOrderId.
     * Adapter will implement actual movement creation + stock recalculation + valuation.
     */
    void recordEntry(Long productId, Integer quantity, Long supplierOrderId);
}
