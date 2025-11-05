package com.tricol.manage_supplier_orders.order.application.service;

import com.tricol.manage_supplier_orders.order.application.ports.*;
import com.tricol.manage_supplier_orders.order.domain.enums.OrderStatus;
import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrder;
import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class SupplierOrderService implements SupplierOrderServicePort {

    private final SupplierOrderRepository repository;
    private final ProductPort productPort;
    private final SupplierPort supplierPort;
    private final StockPort stockPort;

    public SupplierOrderService(SupplierOrderRepository repository,
                                ProductPort productPort,
                                SupplierPort supplierPort,
                                StockPort stockPort) {
        this.repository = repository;
        this.productPort = productPort;
        this.supplierPort = supplierPort;
        this.stockPort = stockPort;
    }

    @Transactional
    @Override
    public SupplierOrder createOrder(SupplierOrder order) {
        // basic validations
        if (order.getSupplier().getId() == null || !supplierPort.existsById(order.getSupplier().getId()).orElse(false)) {
            throw new IllegalArgumentException("Supplier not found: " + order.getSupplier().getId());
        }

        // populate unitPrice from product if missing and compute subtotal
        for (SupplierOrderItem item : order.getItems()) {
            if (item.getUnitPrice() == null) {
                BigDecimal price = productPort.getPriceById(item.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product price not found: " + item.getProductId()));
                item.setUnitPrice(price);
            }
            item.recalcSubtotal();
        }

        order.setOrderDate(OffsetDateTime.now());
        order.recalcTotal();
        order.setStatus(OrderStatus.WAITING);
        return repository.save(order);
    }

    @Transactional(readOnly = true)
    @Override
    public SupplierOrder getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<SupplierOrder> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional
    @Override
    public SupplierOrder updateStatus(Long orderId, OrderStatus newStatus) {
        SupplierOrder existing = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        OrderStatus prev = existing.getStatus();
        existing.setStatus(newStatus);

        // when delivered, create stock entries
        if (newStatus == OrderStatus.DELIVERED && prev != OrderStatus.DELIVERED) {
            for (SupplierOrderItem item : existing.getItems()) {
                stockPort.recordEntry(item.getProductId(), item.getQuantity(), existing.getId());
            }
        }

        return repository.save(existing);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
