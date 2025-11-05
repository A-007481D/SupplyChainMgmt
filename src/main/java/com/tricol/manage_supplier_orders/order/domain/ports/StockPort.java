package com.tricol.manage_supplier_orders.order.domain.ports;

public interface StockPort {
    void increaseStock(Long productId, int quantity);
    void decreaseStock(Long productId, int quantity);
    boolean hasSufficientStock(Long productId, int requiredQuantity);
    void recordEntry(Long productId, Integer quantity, Long orderId);
    boolean checkAvailability(Long productId, Integer requiredQty);

}
