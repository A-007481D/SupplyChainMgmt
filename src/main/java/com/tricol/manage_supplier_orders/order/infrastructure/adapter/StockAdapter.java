package com.tricol.manage_supplier_orders.order.infrastructure.adapter;

import com.tricol.manage_supplier_orders.order.domain.ports.StockPort;
import org.springframework.stereotype.Component;

@Component
public class StockAdapter implements StockPort {

    @Override
    public void recordEntry(Long productId, Integer quantity, Long orderId) {
        // TODO: integrate with Stock module to log incoming stock (ex: via repository or event)
        System.out.printf("Recording stock entry for product %d, qty %d, from order %d%n", productId, quantity, orderId);
    }

    @Override
    public boolean checkAvailability(Long productId, Integer requiredQty) {
        // TODO: integrate with Stock module or inventory service
        return true; // assume available for now
    }

    @Override
    public void increaseStock(Long productId, int quantity) {

    }

    @Override
    public void decreaseStock(Long productId, int quantity) {

    }

    @Override
    public boolean hasSufficientStock(Long productId, int requiredQuantity) {
        return false;
    }
}
