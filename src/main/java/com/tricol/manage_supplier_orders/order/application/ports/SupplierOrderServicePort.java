package com.tricol.manage_supplier_orders.order.application.ports;

import com.tricol.manage_supplier_orders.order.domain.enums.OrderStatus;
import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplierOrderServicePort {
    SupplierOrder createOrder(SupplierOrder order);
    SupplierOrder getById(Long id);
    Page<SupplierOrder> list(Pageable pageable);
    SupplierOrder updateStatus(Long orderId, OrderStatus newStatus);
    void delete(Long id);
}
