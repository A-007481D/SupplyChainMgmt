package com.tricol.manage_supplier_orders.product.domain.ports;

import com.tricol.manage_supplier_orders.product.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {

    Product save(Product product);

    Optional<Product> findById(Long id);

    void deleteById(Long id);

    Page<Product> findAll(Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Product> findBySupplierId(Long supplierId);

    List<Product> findLowStockProducts(Integer threshold);
}
