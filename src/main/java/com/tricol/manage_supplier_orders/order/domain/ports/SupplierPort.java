package com.tricol.manage_supplier_orders.order.domain.ports;

import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import java.util.Optional;

public interface SupplierPort {
    boolean existsById(Long supplierId);
    Optional<Supplier> findById(Long supplierId);
}
