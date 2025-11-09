package com.tricol.manage_supplier_orders.order.infrastructure.adapter;

import com.tricol.manage_supplier_orders.order.application.ports.StockPort;
import com.tricol.manage_supplier_orders.product.domain.model.Product;
import com.tricol.manage_supplier_orders.product.domain.ports.ProductRepositoryPort;
import com.tricol.manage_supplier_orders.stock.application.ports.StockServicePort;
import com.tricol.manage_supplier_orders.stock.domain.enums.MovementType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Adapter that integrates the Order module with the Stock module.
 * Delegates to StockServicePort for actual stock management.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StockAdapter implements StockPort {
    
    private final StockServicePort stockService;
    private final ProductRepositoryPort productRepository;

    @Override
    public void recordEntry(Long productId, Integer quantity, Long supplierOrderId, BigDecimal unitPrice) {
        log.info("Recording stock entry - Product: {}, Quantity: {}, Order: {}, Unit Price: {}", 
                productId, quantity, supplierOrderId, unitPrice);
        
        stockService.recordMovement(
            productId,
            MovementType.ENTRY,
            quantity,
            unitPrice.doubleValue(),
            supplierOrderId
        );
        
        log.info("Stock entry recorded successfully for product {}", productId);
    }
    
    @Override
    public void recordMovement(Long productId, int quantity, String movementType, Long reference, BigDecimal unitPrice) {
        log.info("Recording {} movement - Product: {}, Quantity: {}, Reference: {}", 
                movementType, productId, quantity, reference);
        
        MovementType type = MovementType.valueOf(movementType);
        stockService.recordMovement(
            productId,
            type,
            quantity,
            unitPrice != null ? unitPrice.doubleValue() : null,
            reference
        );
    }
    
    @Override
    public int getCurrentStock(Long productId) {
        return productRepository.findById(productId)
            .map(Product::getStockQuantity)
            .orElse(0);
    }
    
    @Override
    public boolean hasSufficientStock(Long productId, int requiredQuantity) {
        return getCurrentStock(productId) >= requiredQuantity;
    }
    
    @Override
    public BigDecimal getAverageUnitCost(Long productId) {
        return productRepository.findById(productId)
            .map(Product::getAverageCost)
            .map(BigDecimal::valueOf)
            .orElse(BigDecimal.ZERO);
    }
    
    @Override
    public BigDecimal getInventoryValue() {
        // caching || aggre
        return BigDecimal.ZERO; // just a placeholder for later impl
    }
}
