package com.tricol.manage_supplier_orders.order.application.ports;

import java.math.BigDecimal;
import java.util.List;

public interface StockPort {

    /**
     * Record an entry movement for productId and quantity, associated to supplierOrderId.
     * This will also update the current stock and recalculate the average cost if using CUMP.
     */
    void recordEntry(Long productId, Integer quantity, Long supplierOrderId, BigDecimal unitPrice);
    
    /**
     * Record a stock movement (entry, exit, or adjustment).
     * @param productId ID of the product
     * @param quantity Positive for entry, negative for exit
     * @param movementType Type of movement (ENTRY, EXIT, ADJUSTMENT)
     * @param reference Reference ID (e.g., order ID)
     * @param unitPrice Unit price for valuation (required for ENTRY)
     */
    void recordMovement(Long productId, int quantity, String movementType, Long reference, BigDecimal unitPrice);
    
    /**
     * Get current stock level for a product
     */
    int getCurrentStock(Long productId);
    
    /**
     * Check if there's sufficient stock available
     */
    boolean hasSufficientStock(Long productId, int requiredQuantity);
    
    /**
     * Get the current average unit cost for a product (for CUMP method)
     */
    BigDecimal getAverageUnitCost(Long productId);
    
    /**
     * Get the total value of current inventory
     */
    BigDecimal getInventoryValue();
}
