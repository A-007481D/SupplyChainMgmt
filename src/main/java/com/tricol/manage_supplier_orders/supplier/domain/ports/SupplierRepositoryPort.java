package com.tricol.manage_supplier_orders.supplier.domain.ports;

import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SupplierRepositoryPort {
    Supplier createSupplier(Supplier supplier);
    Optional<Supplier> findSupplierById(Long id);
    List<Supplier> findByEmailEndingWith(String emailSuffix);
    List<Supplier> findAllByCompany();
    void deleteSupplierById(Long id);
    Page<Supplier> findAll(Pageable pageable);
}
