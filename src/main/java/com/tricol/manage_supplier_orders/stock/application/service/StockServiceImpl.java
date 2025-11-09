package com.tricol.manage_supplier_orders.stock.application.service;

import com.tricol.manage_supplier_orders.stock.application.ports.StockServicePort;
import com.tricol.manage_supplier_orders.stock.domain.enums.MovementType;
import com.tricol.manage_supplier_orders.stock.domain.enums.StockValuationMethod;
import com.tricol.manage_supplier_orders.stock.domain.exception.InsufficientStockException;
import com.tricol.manage_supplier_orders.stock.domain.model.StockMovement;
import com.tricol.manage_supplier_orders.stock.domain.ports.StockMovementRepository;
import com.tricol.manage_supplier_orders.product.domain.model.Product;
import com.tricol.manage_supplier_orders.product.domain.ports.ProductRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class StockServiceImpl implements StockServicePort {

    private final StockMovementRepository stockRepository;
    private final ProductRepositoryPort productRepository;
    @Value("${stock.valuation.method:FIFO}")
    private StockValuationMethod valuationMethod;

    public StockServiceImpl(StockMovementRepository stockRepository, ProductRepositoryPort productRepository) {
        this.stockRepository = stockRepository;
        this.productRepository = productRepository;
    }

    @Override
    public StockMovement recordMovement(Long productId, MovementType type, Integer quantity, Double unitCost, Long supplierOrderId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        
        StockMovement movement = StockMovement.builder()
                .productId(productId)
                .type(type)
                .quantity(quantity)
                .unitCost(unitCost)
                .totalCost(unitCost != null && quantity != null ? unitCost * quantity : null)
                .movementDate(OffsetDateTime.now())
                .supplierOrderId(supplierOrderId)
                .build();
        
        // Validate stock availability for EXIT and ADJUSTMENT
        validateStockMovement(product, movement);
        
        double currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        double newStock = type == MovementType.ENTRY ? 
                         currentStock + quantity : 
                         currentStock - quantity;
        
        // For FIFO, initialize remaining quantity for ENTRY movements
        if (type == MovementType.ENTRY && valuationMethod == StockValuationMethod.FIFO) {
            movement.setRemainingQuantity((double) quantity);
        }
        
        product.setStockQuantity((int) Math.round(newStock));
        
        if (type == MovementType.ENTRY) {
            double previousQty = currentStock;
            double newCost = calculateCost(product, movement, previousQty);
            product.setAverageCost(newCost);
        }
        
        productRepository.save(product);
        
        return stockRepository.save(movement);
    }

    @Override
    public Page<StockMovement> listMovements(Pageable pageable) {
        return stockRepository.findAll(pageable);
    }

    @Override
    public Page<StockMovement> listByProduct(Long productId, Pageable pageable) {
        return stockRepository.findByProductId(productId, pageable);
    }

    @Override
    public Page<StockMovement> listBySupplierOrder(Long orderId, Pageable pageable) {
        return stockRepository.findBySupplierOrderId(orderId, pageable);
    }

    @Override
    public void handleOrderDelivery(Long orderId, List<StockMovement> movements) {
        for (StockMovement movement : movements) {
            Product product = productRepository.findById(movement.getProductId())
                    .orElseThrow(() -> new RuntimeException("Produit introuvable : " + movement.getProductId()));

            double previousQty = product.getStockQuantity() != null ? product.getStockQuantity() : 0.0;
            double movementQty = movement.getQuantity() != null ? movement.getQuantity() : 0.0;
            
            // For FIFO, initialize remaining quantity for ENTRY movements
            if (movement.getType() == MovementType.ENTRY && valuationMethod == StockValuationMethod.FIFO) {
                movement.setRemainingQuantity(movementQty);
            }
            
            // Calculate new quantity
            double newQty = movement.getType() == MovementType.ENTRY ? 
                           previousQty + movementQty : 
                           previousQty - movementQty;

            double newUnitCost = calculateCost(product, movement, previousQty);

            product.setStockQuantity((int) Math.round(newQty));
            product.setAverageCost(newUnitCost);
            productRepository.save(product);

            movement.setSupplierOrderId(orderId);
            stockRepository.save(movement);
        }
    }
    
    private void validateStockMovement(Product product, StockMovement movement) {
        if (movement.getType() != MovementType.ENTRY) {
            double currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
            double movementQty = movement.getQuantity() != null ? movement.getQuantity() : 0;
            
            if (currentStock < movementQty) {
                throw new InsufficientStockException(
                    String.format("Insufficient stock for product %s. Available: %.2f, Requested: %.2f",
                        product.getId(), currentStock, movementQty)
                );
            }
        }
    }

    private double calculateCost(Product product, StockMovement movement, double previousQty) {
        if (movement.getType() == MovementType.ENTRY) {
            // For entries, calculate new average cost based on valuation method
            if (valuationMethod == StockValuationMethod.CUMP) {
                return calculateCumpCost(product, movement, previousQty);
            } else { // FIFO
                // For FIFO, we just store the movement - cost is calculated on exit
                return product.getAverageCost() != null ? product.getAverageCost() : movement.getUnitCost();
            }
        } else { // EXIT or ADJUSTMENT
            if (valuationMethod == StockValuationMethod.FIFO) {
                return calculateFifoCost(product, movement, previousQty);
            } else {
                // For CUMP, cost is already the average
                return product.getAverageCost() != null ? product.getAverageCost() : 0.0;
            }
        }
    }

    private double calculateCumpCost(Product product, StockMovement movement, double previousQty) {
        double totalBefore = (product.getAverageCost() != null ? product.getAverageCost() : 0.0) * previousQty;
        double totalNew = (movement.getUnitCost() != null ? movement.getUnitCost() : 0.0) *
                (movement.getQuantity() != null ? movement.getQuantity() : 0.0);
        double totalQty = previousQty + (movement.getQuantity() != null ? movement.getQuantity() : 0.0);
        return totalQty > 0 ? (totalBefore + totalNew) / totalQty :
                (product.getAverageCost() != null ? product.getAverageCost() : 0.0);
    }

    private double calculateFifoCost(Product product, StockMovement movement, double previousQty) {
        List<StockMovement> entries = stockRepository.findByProductIdAndTypeOrderByMovementDateAsc(
                movement.getProductId(),
                MovementType.ENTRY
        );

        double remainingQty = movement.getQuantity() != null ? movement.getQuantity() : 0;
        double totalCost = 0.0;

        for (StockMovement entry : entries) {
            if (remainingQty <= 0) break;

            double availableQty = entry.getRemainingQuantity() > 0 ?
                    entry.getRemainingQuantity() :
                    (entry.getQuantity() != null ? entry.getQuantity() : 0);

            if (availableQty > 0) {
                double usedQty = Math.min(availableQty, remainingQty);
                totalCost += usedQty * (entry.getUnitCost() != null ? entry.getUnitCost() : 0);
                remainingQty -= usedQty;

                // Update remaining quantity in the entry
                entry.setRemainingQuantity(availableQty - usedQty);
                stockRepository.save(entry);
            }
        }

        if (remainingQty > 0) {
            // If we still have quantity to account for (shouldn't happen with proper validation)
            log.warn("Insufficient stock entries to cover FIFO calculation for product {}",
                    movement.getProductId());
            // Use current average cost for remaining
            totalCost += remainingQty * (product.getAverageCost() != null ?
                    product.getAverageCost() : 0);
        }

        return movement.getQuantity() != null && movement.getQuantity() > 0 ?
                totalCost / movement.getQuantity() : 0.0;
    }
}
