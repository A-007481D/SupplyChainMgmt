package com.tricol.manage_supplier_orders.order.infrastructure.persistence.repository;

import com.tricol.manage_supplier_orders.order.infrastructure.persistence.entity.SupplierOrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierOrderJpaRepository extends JpaRepository<SupplierOrderJpaEntity, Long> {

}
