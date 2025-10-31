package com.tricol.manage_supplier_orders.supplier.application.service;

import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import com.tricol.manage_supplier_orders.supplier.api.mapper.SupplierApiMapper;
import com.tricol.manage_supplier_orders.supplier.application.ports.SupplierServicePort;
import com.tricol.manage_supplier_orders.supplier.domain.ports.SupplierRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierServiceImpl implements SupplierServicePort {

    private final SupplierRepositoryPort supplierRepositoryPort;
//    private final SupplierApiMapper apiMapper; // was loosely coupled to API layer, now returning domain objects directly

    public SupplierServiceImpl(SupplierRepositoryPort supplierRepositoryPort/*, SupplierApiMapper apiMapper*/) {
        this.supplierRepositoryPort = supplierRepositoryPort;
//        this.apiMapper = apiMapper;
    }

    @Transactional
    @Override
    public Supplier create(CreateSupplierCommand cmd) {
        Supplier domain = Supplier.builder()
                .company(cmd.getCompany())
                .address(cmd.getAddress())
                .contact(cmd.getContact())
                .email(cmd.getEmail())
                .phone(cmd.getPhone())
                .city(cmd.getCity())
                .ice(cmd.getIce())
                .build();
        return supplierRepositoryPort.createSupplier(domain);
//        return apiMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Supplier> list(Pageable pageable) {
        return supplierRepositoryPort.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Supplier getById(Long id) { // <-- Return Supplier
        return supplierRepositoryPort.findSupplierById(id) // <-- Return domain object
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + id));
    }

    @Transactional
    @Override
    public Supplier update(Long id, CreateSupplierCommand cmd) {
        Supplier existing = supplierRepositoryPort.findSupplierById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + id));

        existing.setCompany(cmd.getCompany());
        existing.setAddress(cmd.getAddress());
        existing.setContact(cmd.getContact());
        existing.setEmail(cmd.getEmail());
        existing.setPhone(cmd.getPhone());
        existing.setCity(cmd.getCity());
        existing.setIce(cmd.getIce());

        return supplierRepositoryPort.createSupplier(existing);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        supplierRepositoryPort.deleteSupplierById(id);
    }
}
