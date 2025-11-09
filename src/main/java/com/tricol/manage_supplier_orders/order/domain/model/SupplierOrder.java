package com.tricol.manage_supplier_orders.order.domain.model;


import com.tricol.manage_supplier_orders.order.domain.enums.OrderStatus;
import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SupplierOrder {
    private Long id;
    private OffsetDateTime orderDate;
    private OrderStatus status;
    private Supplier supplier;
    private BigDecimal totalAmount;
    private List<SupplierOrderItem> items = new ArrayList<>();

    public void recalcTotal() {
        this.totalAmount = items.stream()
                .map(SupplierOrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addItem(SupplierOrderItem item) {
        this.items.add(item);
        recalcTotal();
    }

    public void removeItem(SupplierOrderItem item) {
        items.remove(item);
        recalcTotal();
    }


}
