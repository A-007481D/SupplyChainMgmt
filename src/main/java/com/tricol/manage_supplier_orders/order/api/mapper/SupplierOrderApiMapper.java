package com.tricol.manage_supplier_orders.order.api.mapper;

import com.tricol.manage_supplier_orders.order.api.dto.CreateSupplierOrderRequest;
import com.tricol.manage_supplier_orders.order.api.dto.SupplierOrderDTO;
import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplierOrderApiMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    SupplierOrder toDomain(CreateSupplierOrderRequest req);

    SupplierOrderDTO toDto(SupplierOrder order);
}
