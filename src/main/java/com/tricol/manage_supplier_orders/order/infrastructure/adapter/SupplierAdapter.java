package com.tricol.manage_supplier_orders.order.infrastructure.adapter;

import com.tricol.manage_supplier_orders.order.domain.ports.SupplierPort;
import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SupplierAdapter implements SupplierPort {

    @Override
    public boolean existsById(Long supplierId) {
        return true;
    }

    @Override
    public Optional<Supplier> findById(Long supplierId) {
        return Optional.empty();
    }

    // You can add more methods from SupplierPort here as needed
}
