package com.tricol.manage_supplier_orders.order.application.ports;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductPort {
    Optional<BigDecimal> getPriceById(Long productId);
}
