package com.tricol.manage_supplier_orders.product.application.ports;

import com.tricol.manage_supplier_orders.product.domain.enums.Category;
import com.tricol.manage_supplier_orders.product.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface ProductServicePort {

    Product createProduct(Product product);

    Product updateProduct(Long productId, Product product);

    void deleteProduct(Long productId);

    Product getProductById(Long productId);

    Page<Product> getAllProducts(Pageable pageable);

    Page<Product> searchProductsByName(String name, Pageable pageable);

    List<Product> getProductsBySupplier(Long supplierId);

    List<Product> getLowStockProducts(Integer threshold);

    List<Product> getProductsByCategory(Category category);
}
