package com.tricol.manage_supplier_orders.order.api.mapper;

import com.tricol.manage_supplier_orders.order.api.dto.CreateSupplierOrderRequest;
import com.tricol.manage_supplier_orders.order.api.dto.SupplierOrderDTO;
import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrder;
import com.tricol.manage_supplier_orders.order.application.ports.SupplierPort;
import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import com.tricol.manage_supplier_orders.order.domain.enums.OrderStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class SupplierOrderApiMapper {
    
    @Autowired
    protected SupplierPort supplierPort;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderDate", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "status", expression = "java(com.tricol.manage_supplier_orders.order.domain.enums.OrderStatus.WAITING)")
    @Mapping(target = "supplier", expression = "java(getSupplier(req.getSupplierId()))")
    @Mapping(target = "totalAmount", ignore = true)
    public abstract SupplierOrder toDomain(CreateSupplierOrderRequest req);

    protected Supplier getSupplier(Long supplierId) {
        return supplierPort.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with ID: " + supplierId));
    }

    public abstract SupplierOrderDTO toDto(SupplierOrder order);
}
