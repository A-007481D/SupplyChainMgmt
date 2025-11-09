package com.tricol.manage_supplier_orders.order.api.mapper;

import com.tricol.manage_supplier_orders.order.api.dto.CreateSupplierOrderRequest;
import com.tricol.manage_supplier_orders.order.api.dto.CreateSupplierOrderItemRequest;
import com.tricol.manage_supplier_orders.order.api.dto.SupplierOrderDTO;
import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrder;
import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrderItem;
import com.tricol.manage_supplier_orders.order.application.ports.SupplierPort;
import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import com.tricol.manage_supplier_orders.order.domain.enums.OrderStatus;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {SupplierOrderItemMapper.class})
public abstract class SupplierOrderApiMapper {
    
    @Autowired
    protected SupplierPort supplierPort;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderDate", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "status", expression = "java(com.tricol.manage_supplier_orders.order.domain.enums.OrderStatus.WAITING)")
    @Mapping(target = "supplier", expression = "java(getSupplier(req.getSupplierId()))")
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "items", source = "items")
    public abstract SupplierOrder toDomain(CreateSupplierOrderRequest req);
    
    @AfterMapping
    protected void afterToDomain(CreateSupplierOrderRequest req, @MappingTarget SupplierOrder order) {
        if (order.getItems() != null) {
            for (SupplierOrderItem item : order.getItems()) {
                item.setOrder(order);
                item.recalcSubtotal();
            }
        }
        order.recalcTotal();
    }
    
    @Mapping(target = "supplierId", source = "supplier.id")
    public abstract SupplierOrderDTO toDto(SupplierOrder order);
    
    protected Supplier getSupplier(Long supplierId) {
        if (supplierId == null) {
            return null;
        }
        return supplierPort.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with ID: " + supplierId));
    }
}
