package com.tricol.manage_supplier_orders.order.application.ports;

import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import java.util.Optional;

public interface SupplierPort {
    Optional<Boolean> existsById(Long supplierId);
    Optional<Supplier> findById(Long supplierId);
}
