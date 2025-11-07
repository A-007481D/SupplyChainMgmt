package com.tricol.manage_supplier_orders.stock.infrastructure.persistence.repository;


import com.tricol.manage_supplier_orders.stock.infrastructure.persistence.entity.StockMovementJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StockMovementJpaRepository extends JpaRepository<StockMovementJpaEntity, Long> {
    Page<StockMovementJpaEntity> findByProductId(Long productId, Pageable pageable);
    Page<StockMovementJpaEntity> findBySupplierOrderId(Long orderId, Pageable pageable);
}