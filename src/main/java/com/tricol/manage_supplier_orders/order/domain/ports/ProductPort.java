package com.tricol.manage_supplier_orders.order.domain.ports;

import com.tricol.manage_supplier_orders.product.domain.model.Product;
import java.util.Optional;
import java.util.List;

public interface ProductPort {
    Optional<Product> findById(Long productId);
    List<Product> findAllByIds(List<Long> productIds);
    boolean existsById(Long productId);
}
