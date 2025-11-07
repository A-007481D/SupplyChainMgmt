package com.tricol.manage_supplier_orders.stock.application.ports;


import com.tricol.manage_supplier_orders.stock.domain.enums.MovementType;
import com.tricol.manage_supplier_orders.stock.domain.model.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.awt.event.KeyListener;
import java.util.List;


public interface StockServicePort {
    StockMovement recordMovement(Long productId, MovementType type, Integer quantity, Double unitCost, Long supplierOrderId);
    Page<StockMovement> listMovements(Pageable pageable);
    Page<StockMovement> listByProduct(Long productId, Pageable pageable);
    Page<StockMovement> listBySupplierOrder(Long orderId, Pageable pageable);
    void handleOrderDelivery(Long orderId, List<StockMovement> movements);

}