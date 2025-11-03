package com.tricol.manage_supplier_orders.product.infrastructure.persistence.repository;

import com.tricol.manage_supplier_orders.product.domain.enums.Category;
import com.tricol.manage_supplier_orders.product.infrastructure.persistence.entity.ProductJpaEntity;
import com.tricol.manage_supplier_orders.supplier.infrastructure.persistence.entity.SupplierJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {

    Page<ProductJpaEntity> findByCompanyContainingIgnoreCase(String company, Pageable pageable);
    List<ProductJpaEntity> find(String emailSuffix);

    List<ProductJpaEntity> findProductsByCategory(Category category, Sort sort);
}