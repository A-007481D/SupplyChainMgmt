package com.tricol.manage_supplier_orders.supplier.infrastructure.persistence.repository;


import com.tricol.manage_supplier_orders.supplier.infrastructure.persistence.entity.SupplierJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupplierJpaRepository extends JpaRepository<SupplierJpaEntity, Long> {
    Page<SupplierJpaEntity> findByCompanyContainingIgnoreCase(String company, Pageable pageable);
    List<SupplierJpaEntity> findByEmailEndingWith(String emailSuffix);
    List<SupplierJpaEntity> findAllByOrderByCompanyAsc();
}
