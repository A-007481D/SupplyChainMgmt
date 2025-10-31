package com.tricol.manage_supplier_orders.supplier.infrastructure.adapter;

import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import com.tricol.manage_supplier_orders.supplier.domain.ports.SupplierRepositoryPort;
import com.tricol.manage_supplier_orders.supplier.infrastructure.persistence.entity.SupplierJpaEntity;
import com.tricol.manage_supplier_orders.supplier.infrastructure.persistence.mapper.SupplierJpaMapper;
import com.tricol.manage_supplier_orders.supplier.infrastructure.persistence.repository.SupplierJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SupplierRepositoryPortAdapter implements SupplierRepositoryPort {

    private final SupplierJpaRepository jpaRepository;
    private final SupplierJpaMapper jpaMapper;

    public SupplierRepositoryPortAdapter(SupplierJpaRepository jpaRepository, SupplierJpaMapper jpaMapper) {
        this.jpaRepository = jpaRepository;
        this.jpaMapper = jpaMapper;
    }

    @Override
    public Supplier createSupplier(Supplier supplier) {
        SupplierJpaEntity entity = jpaMapper.toEntity(supplier);
        SupplierJpaEntity saved = jpaRepository.save(entity);
        return jpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Supplier> findSupplierById(Long id) {
        return jpaRepository.findById(id).map(jpaMapper::toDomain);
    }

    @Override
    public List<Supplier> findByEmailEndingWith(String emailSuffix) {
        return jpaRepository.findByEmailEndingWith(emailSuffix)
                .stream()
                .map(jpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<Supplier> findAllByCompany() {
        return jpaRepository.findAllByOrderByCompanyAsc()
                .stream()
                .map(jpaMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteSupplierById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Page<Supplier> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(jpaMapper::toDomain);
    }
}
