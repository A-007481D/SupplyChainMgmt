package com.tricol.manage_supplier_orders.order.api.mapper;

import com.tricol.manage_supplier_orders.order.api.dto.CreateSupplierOrderItemRequest;
import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplierOrderItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    SupplierOrderItem toDomain(CreateSupplierOrderItemRequest request);
}
