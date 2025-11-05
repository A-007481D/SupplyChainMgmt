package com.tricol.manage_supplier_orders.order.domain.ports;

import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SupplierOrderRepository {
    SupplierOrder save(SupplierOrder order);
    Optional<SupplierOrder> findById(Long id);
    Page<SupplierOrder> findAll(Pageable pageable);
    void deleteById(Long id);
}
