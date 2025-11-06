package com.tricol.manage_supplier_orders.order.infrastructure.adapter;

import com.tricol.manage_supplier_orders.order.application.ports.SupplierPort;
import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Temporary implementation that assumes all suppliers exist.
 * In a real application, this would call the Supplier service/API to verify existence.
 */
@Component
public class SupplierAdapter implements SupplierPort {

    @Override
    public Optional<Boolean> existsById(Long supplierId) {
        // For development/testing, assume supplier exists if ID is positive
        return Optional.of(supplierId != null && supplierId > 0);
    }

    @Override
    public Optional<Supplier> findById(Long supplierId) {
        // For development/testing, create a dummy supplier with the given ID
        if (supplierId != null && supplierId > 0) {
            Supplier supplier = Supplier.builder()
                    .id(supplierId)
                    .company("Dummy Supplier " + supplierId)
                    .contact("Contact Person")
                    .email("dummy" + supplierId + "@example.com")
                    .phone("+1234567890")
                    .address("123 Dummy St.")
                    .city("Dummy City")
                    .ice("ICE" + supplierId)
                    .build();
            return Optional.of(supplier);
        }
        return Optional.empty();
    }
}
