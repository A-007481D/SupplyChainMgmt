package com.tricol.manage_supplier_orders.stock.domain.ports;


import com.tricol.manage_supplier_orders.stock.domain.model.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.Optional;


public interface StockMovementRepository {
    StockMovement save(StockMovement movement);
    Optional<StockMovement> findById(Long id);
    Page<StockMovement> findAll(Pageable pageable);
    Page<StockMovement> findByProductId(Long productId, Pageable pageable);
    Page<StockMovement> findBySupplierOrderId(Long orderId, Pageable pageable);
}