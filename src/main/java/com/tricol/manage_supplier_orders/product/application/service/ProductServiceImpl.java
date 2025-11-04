package com.tricol.manage_supplier_orders.product.application.service;

import com.tricol.manage_supplier_orders.product.application.ports.ProductServicePort;
import com.tricol.manage_supplier_orders.product.domain.enums.Category;
import com.tricol.manage_supplier_orders.product.domain.model.Product;
import com.tricol.manage_supplier_orders.product.domain.ports.ProductRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductServicePort {

    private final ProductRepositoryPort productRepositoryPort;

    public ProductServiceImpl(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Transactional
    @Override
    public Product createProduct(Product product) {
        return productRepositoryPort.save(product);
    }

    @Transactional
    @Override
    public Product updateProduct(Long productId, Product product) {
        Product existing = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setCategory(product.getCategory());
        existing.setUnit(product.getUnit());
        existing.setStockQuantity(product.getStockQuantity());
        existing.setSupplierId(product.getSupplierId());

        return productRepositoryPort.save(existing);
    }

    @Transactional
    @Override
    public void deleteProduct(Long productId) {
        productRepositoryPort.deleteById(productId);
    }

    @Transactional(readOnly = true)
    @Override
    public Product getProductById(Long productId) {
        return productRepositoryPort.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepositoryPort.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Product> searchProductsByName(String name, Pageable pageable) {
        return productRepositoryPort.findByNameContainingIgnoreCase(name, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Product> getProductsBySupplier(Long supplierId) {
        return productRepositoryPort.findBySupplierId(supplierId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepositoryPort.findLowStockProducts(threshold);
    }

    @Override
    public List<Product> getProductsByCategory(Category category) {
        return productRepositoryPort.findProductsByCategory(category);
    }
}
