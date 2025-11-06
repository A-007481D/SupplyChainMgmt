package com.tricol.manage_supplier_orders.order.infrastructure.adapter;

import com.tricol.manage_supplier_orders.order.application.ports.StockPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * In-memory implementation of StockPort for development.
 * In production, this would integrate with a persistent storage and proper transaction management.
 */
@Component
public class StockAdapter implements StockPort {
    private static final Logger log = LoggerFactory.getLogger(StockAdapter.class);
    
    @Value("${inventory.valuation.method:CUMP}")
    private String valuationMethod;
    
    // In-memory storage for development (replace with database in production)
    private final Map<Long, AtomicInteger> stockLevels = new HashMap<>();
    private final Map<Long, AtomicReference<BigDecimal>> averageCosts = new HashMap<>();
    private final Map<Long, BigDecimal> lastPurchasePrices = new HashMap<>();

    @Override
    public void recordEntry(Long productId, Integer quantity, Long supplierOrderId, BigDecimal unitPrice) {
        log.info("Recording stock entry - Product: {}, Quantity: {}, Order: {}, Unit Price: {}", 
                productId, quantity, supplierOrderId, unitPrice);
        
        // Record the movement
        recordMovement(productId, quantity, "ENTRY", supplierOrderId, unitPrice);
        
        // Update last purchase price
        lastPurchasePrices.put(productId, unitPrice);
        
        // Update stock level
        int currentStock = getCurrentStock(productId);
        stockLevels.put(productId, new AtomicInteger(currentStock + quantity));
        
        // Update average cost if using CUMP method
        if ("CUMP".equals(valuationMethod)) {
            updateAverageCost(productId, quantity, unitPrice);
        }
    }
    
    @Override
    public void recordMovement(Long productId, int quantity, String movementType, Long reference, BigDecimal unitPrice) {
        log.info("Recording {} movement - Product: {}, Quantity: {}, Reference: {}", 
                movementType, productId, quantity, reference);
        
        // In a real implementation, this would save to a movements/transactions table
        switch (movementType) {
            case "ENTRY":
                // Already handled in recordEntry
                break;
            case "EXIT":
                int currentStock = getCurrentStock(productId);
                if (currentStock < quantity) {
                    throw new IllegalStateException("Insufficient stock for product: " + productId);
                }
                stockLevels.put(productId, new AtomicInteger(currentStock - quantity));
                break;
            case "ADJUSTMENT":
                // Handle stock adjustments (e.g., inventory count corrections)
                stockLevels.put(productId, new AtomicInteger(quantity));
                break;
            default:
                throw new IllegalArgumentException("Invalid movement type: " + movementType);
        }
    }
    
    @Override
    public int getCurrentStock(Long productId) {
        return stockLevels.getOrDefault(productId, new AtomicInteger(0)).get();
    }
    
    @Override
    public boolean hasSufficientStock(Long productId, int requiredQuantity) {
        return getCurrentStock(productId) >= requiredQuantity;
    }
    
    @Override
    public BigDecimal getAverageUnitCost(Long productId) {
        return averageCosts.getOrDefault(productId, new AtomicReference<>(BigDecimal.ZERO)).get();
    }
    
    @Override
    public BigDecimal getInventoryValue() {
        return stockLevels.entrySet().stream()
                .map(entry -> {
                    BigDecimal unitCost = getAverageUnitCost(entry.getKey());
                    return unitCost.multiply(BigDecimal.valueOf(entry.getValue().get()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private void updateAverageCost(Long productId, int addedQuantity, BigDecimal unitPrice) {
        int currentStock = getCurrentStock(productId);
        BigDecimal currentTotalValue = getAverageUnitCost(productId)
                .multiply(BigDecimal.valueOf(currentStock - addedQuantity));
                
        BigDecimal addedValue = unitPrice.multiply(BigDecimal.valueOf(addedQuantity));
        BigDecimal newTotalValue = currentTotalValue.add(addedValue);
        
        if (currentStock > 0) {
            BigDecimal newAverageCost = newTotalValue.divide(
                    BigDecimal.valueOf(currentStock), 2, java.math.RoundingMode.HALF_UP);
            averageCosts.put(productId, new AtomicReference<>(newAverageCost));
        } else {
            averageCosts.put(productId, new AtomicReference<>(unitPrice));
        }
    }
}
