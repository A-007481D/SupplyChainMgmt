package com.tricol.manage_supplier_orders.stock.application.service;

import com.tricol.manage_supplier_orders.stock.application.ports.StockServicePort;
import com.tricol.manage_supplier_orders.stock.domain.enums.StockValuationMethod;
import com.tricol.manage_supplier_orders.stock.domain.model.StockMovement;
import com.tricol.manage_supplier_orders.stock.domain.ports.StockMovementRepository;
import com.tricol.manage_supplier_orders.product.domain.model.Product;
import com.tricol.manage_supplier_orders.product.domain.ports.ProductRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional
public class StockServiceImpl implements StockServicePort {

    private final StockMovementRepository stockRepository;
    private final ProductRepositoryPort productRepository;
    private final StockValuationMethod valuationMethod = StockValuationMethod.CUMP; // default

    public StockServiceImpl(StockMovementRepository stockRepository, ProductRepositoryPort productRepository) {
        this.stockRepository = stockRepository;
        this.productRepository = productRepository;
    }

    @Override
    public StockMovement recordMovement(Long productId, com.tricol.manage_supplier_orders.stock.domain.enums.MovementType type, Integer quantity, Double unitCost, Long supplierOrderId) {
        StockMovement movement = StockMovement.builder()
                .productId(productId)
                .type(type)
                .quantity(quantity)
                .unitCost(unitCost)
                .totalCost(unitCost != null && quantity != null ? unitCost * quantity : null)
                .movementDate(OffsetDateTime.now())
                .supplierOrderId(supplierOrderId)
                .build();
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

            // current stock and quantities
            double previousQty = product.getStockQuantity() != null ? product.getStockQuantity() : 0.0;
            double movementQty = movement.getQuantity() != null ? movement.getQuantity() : 0.0;
            double newQty = previousQty + movementQty;

            double newUnitCost = calculateCost(product, movement, previousQty);

            product.setStockQuantity((int) Math.round(newQty));
            product.setAverageCost(newUnitCost);
            productRepository.save(product);

            movement.setSupplierOrderId(orderId);
            stockRepository.save(movement);
        }
    }

    private double calculateCost(Product product, StockMovement movement, double previousQty) {
        if (valuationMethod == StockValuationMethod.CUMP) {
            double totalBefore = (product.getAverageCost() != null ? product.getAverageCost() : 0.0) * previousQty;
            double totalNew = (movement.getUnitCost() != null ? movement.getUnitCost() : 0.0) * (movement.getQuantity() != null ? movement.getQuantity() : 0.0);
            double totalQty = previousQty + (movement.getQuantity() != null ? movement.getQuantity() : 0.0);
            return totalQty > 0 ? (totalBefore + totalNew) / totalQty : (product.getAverageCost() != null ? product.getAverageCost() : 0.0);
        }
        // FIFO could be added later
        return product.getAverageCost() != null ? product.getAverageCost() : 0.0;
    }
}
