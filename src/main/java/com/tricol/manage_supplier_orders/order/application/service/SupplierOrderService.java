package com.tricol.manage_supplier_orders.order.application.service;

import com.tricol.manage_supplier_orders.order.application.ports.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.tricol.manage_supplier_orders.order.domain.enums.OrderStatus;
import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrder;
import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierOrderService implements SupplierOrderServicePort {

    private final SupplierOrderRepository repository;
    private final ProductPort productPort;
    private final SupplierPort supplierPort;
    private final StockPort stockPort;

    @Transactional
    @Override
    public SupplierOrder createOrder(SupplierOrder order) {
        validateOrder(order);
        
        // Set default status if not set
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.WAITING);
        }
        
        // Set order date if not set
        if (order.getOrderDate() == null) {
            order.setOrderDate(OffsetDateTime.now());
        }
        
        // Recalculate total amount
        order.recalcTotal();
        
        return repository.save(order);
    }
    
    @Transactional
    @Override
    public SupplierOrder updateStatus(Long id, OrderStatus newStatus) {
        SupplierOrder order = getById(id);
        OrderStatus currentStatus = order.getStatus();
        
        validateStatusTransition(currentStatus, newStatus);
        
        order.setStatus(newStatus);
        
        // Update stock when order is delivered
        if (newStatus == OrderStatus.DELIVERED && currentStatus != OrderStatus.DELIVERED) {
            updateStockForOrder(order);
        }
        
        return repository.save(order);
    }
    
    private void updateStockForOrder(SupplierOrder order) {
        for (SupplierOrderItem item : order.getItems()) {
            try {
                stockPort.recordEntry(
                    item.getProductId(),
                    item.getQuantity(),
                    order.getId(),
                    item.getUnitPrice()
                );
                log.info("Stock updated for product {}: +{} units", item.getProductId(), item.getQuantity());
            } catch (Exception e) {
                log.error("Failed to update stock for product {}: {}", item.getProductId(), e.getMessage());
                throw new IllegalStateException("Failed to update stock: " + e.getMessage(), e);
            }
        }
    }
    
    private void validateOrder(SupplierOrder order) {
        if (order.getSupplier() == null || order.getSupplier().getId() == null) {
            throw new IllegalArgumentException("Supplier is required");
        }
        
        if (!supplierPort.existsById(order.getSupplier().getId()).orElse(false)) {
            throw new IllegalArgumentException("Supplier not found: " + order.getSupplier().getId());
        }
        
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        
        for (SupplierOrderItem item : order.getItems()) {
            if (item.getProductId() == null) {
                throw new IllegalArgumentException("Product ID is required for all items");
            }
            
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0 for product: " + item.getProductId());
            }
            
            // If unit price is not provided, try to get it from the product
            if (item.getUnitPrice() == null) {
                BigDecimal price = productPort.getPriceById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product price not found for product: " + item.getProductId()));
                item.setUnitPrice(price);
            } else if (item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Unit price must be greater than 0 for product: " + item.getProductId());
            }
            
            item.recalcSubtotal();
        }
    }
    
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == newStatus) {
            return; // No change, nothing to validate
        }
        
        if (currentStatus == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of a CANCELLED order");
        }
        
        if (currentStatus == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot change status of a DELIVERED order");
        }
        
        // Validate status transitions
        switch (newStatus) {
            case WAITING:
                // Can only transition to WAITING from itself (no-op)
                break;
            case VALIDATED:
                // Can validate from WAITING
                if (currentStatus != OrderStatus.WAITING) {
                    throw new IllegalStateException("Can only validate orders in WAITING status");
                }
                break;
            case DELIVERED:
                // Can deliver from VALIDATED
                if (currentStatus != OrderStatus.VALIDATED) {
                    throw new IllegalStateException("Can only deliver orders in VALIDATED status");
                }
                break;
            case CANCELLED:
                // Can cancel from WAITING or VALIDATED
                if (currentStatus != OrderStatus.WAITING && currentStatus != OrderStatus.VALIDATED) {
                    throw new IllegalStateException("Can only cancel orders in WAITING or VALIDATED status");
                }
                break;
            default:
                throw new IllegalStateException("Unknown status: " + newStatus);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public SupplierOrder getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<SupplierOrder> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
