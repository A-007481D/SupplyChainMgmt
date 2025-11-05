package com.tricol.manage_supplier_orders.product.infrastructure.adapter;

import com.tricol.manage_supplier_orders.product.domain.enums.Category;
import com.tricol.manage_supplier_orders.product.domain.model.Product;
import com.tricol.manage_supplier_orders.product.domain.ports.ProductRepositoryPort;
import com.tricol.manage_supplier_orders.product.infrastructure.persistence.entity.ProductJpaEntity;
import com.tricol.manage_supplier_orders.product.infrastructure.persistence.mapper.ProductJpaMapper;
import com.tricol.manage_supplier_orders.product.infrastructure.persistence.repository.ProductJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductRepositoryPortAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository jpaRepository;
    private final ProductJpaMapper jpaMapper;

    public ProductRepositoryPortAdapter(ProductJpaRepository jpaRepository, ProductJpaMapper jpaMapper) {
        this.jpaRepository = jpaRepository;
        this.jpaMapper = jpaMapper;
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity jpaEntity = jpaMapper.toEntity(product);
        ProductJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return jpaMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(Long id) {

        return jpaRepository.findById(id).map(jpaMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(jpaMapper::toDomain);
    }

    @Override
    public Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable) {
        return jpaRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(jpaMapper::toDomain);
    }

    @Override
    public List<Product> findBySupplierId(Long supplierId) {
        return jpaRepository.findBySupplierId(supplierId).stream()
                .map(jpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<Product> findLowStockProducts(Integer threshold) {
        return jpaRepository.findAll().stream()
                .map(jpaMapper::toDomain)
                .filter(product -> product.getStockQuantity() < threshold)
                .toList();
    }




    @Override
    public List<Product> findProductsByCategory(Category category){
        return jpaRepository.findProductsByCategory(Category.valueOf(category.name()), Sort.by("name").ascending())
                .stream()
                .map(jpaMapper::toDomain)
                .toList();
    }
}
