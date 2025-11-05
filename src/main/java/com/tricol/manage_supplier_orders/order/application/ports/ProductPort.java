package com.tricol.manage_supplier_orders.order.application.ports;

import java.math.BigDecimal;
import java.util.Optional;

// Minimal product-port used by order service to fetch product price etc.
public interface ProductPort {
    Optional<BigDecimal> getPriceById(Long productId);
    // optionally: Optional<ProductSummary> findById(Long productId);
}
