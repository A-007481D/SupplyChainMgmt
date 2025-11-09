package com.tricol.manage_supplier_orders.order.infrastructure.adapter;

import com.tricol.manage_supplier_orders.order.application.ports.SupplierOrderRepository;
import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrder;
import com.tricol.manage_supplier_orders.order.infrastructure.persistence.mapper.SupplierOrderJpaMapper;
import com.tricol.manage_supplier_orders.order.infrastructure.persistence.entity.SupplierOrderJpaEntity;
import com.tricol.manage_supplier_orders.order.infrastructure.persistence.repository.SupplierOrderJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SupplierOrderRepositoryAdapter implements SupplierOrderRepository {

    private final SupplierOrderJpaRepository jpa;
    private final SupplierOrderJpaMapper mapper;

    public SupplierOrderRepositoryAdapter(SupplierOrderJpaRepository jpa, SupplierOrderJpaMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public SupplierOrder save(SupplierOrder order) {
        SupplierOrderJpaEntity entity = mapper.toEntity(order);
        SupplierOrderJpaEntity saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SupplierOrder> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<SupplierOrder> findAll(Pageable pageable) {
        return jpa.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
