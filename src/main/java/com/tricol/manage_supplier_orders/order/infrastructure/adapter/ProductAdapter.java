package com.tricol.manage_supplier_orders.order.infrastructure.adapter;

import com.tricol.manage_supplier_orders.order.domain.ports.ProductPort;
import org.springframework.stereotype.Component;

@Component
public class ProductAdapter implements ProductPort {

    @Override
    public boolean existsById(Long productId) {
        // TODO: connect to Product module to check existence
        return false;
    }

    @Override
    public java.util.Optional<com.tricol.manage_supplier_orders.product.domain.model.Product> findById(Long productId) {
        // TODO: call ProductRepositoryPortAdapter or REST API
        return java.util.Optional.empty();
    }

    @Override
    public java.util.List<com.tricol.manage_supplier_orders.product.domain.model.Product> findAllByIds(java.util.List<Long> productIds) {
        // TODO: implement cross-module call
        return java.util.Collections.emptyList();
    }
}
