package com.tricol.manage_supplier_orders.order.infrastructure.adapter;

import com.tricol.manage_supplier_orders.order.application.ports.ProductPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class ProductAdapter implements ProductPort {

    @Override
    public Optional<BigDecimal> getPriceById(Long productId) {
        // TODO: Implement actual price lookup from product module
        // For now, return a default price for testing
        return Optional.of(BigDecimal.TEN);
    }
}
