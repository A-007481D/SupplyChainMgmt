package com.tricol.manage_supplier_orders.stock.domain.ports;

import com.tricol.manage_supplier_orders.stock.domain.model.StockMovement;
import java.util.List;
import java.util.Optional;

public interface StockRepositoryPort {
    StockMovement save(StockMovement movement);
    List<StockMovement> findByProductId(Long productId);
    List<StockMovement> findByOrderId(Long orderId);
    Optional<StockMovement> findById(Long id);
}
