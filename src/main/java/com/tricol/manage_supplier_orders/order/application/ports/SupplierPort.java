package com.tricol.manage_supplier_orders.order.application.ports;

import java.util.Optional;

public interface SupplierPort {
    Optional<Boolean> existsById(Long supplierId);
}
