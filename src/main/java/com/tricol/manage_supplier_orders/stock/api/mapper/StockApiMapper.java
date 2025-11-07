package com.tricol.manage_supplier_orders.stock.api.mapper;

import com.tricol.manage_supplier_orders.stock.api.dto.CreateStockMovementRequest;
import com.tricol.manage_supplier_orders.stock.api.dto.StockMovementDTO;
import com.tricol.manage_supplier_orders.stock.domain.model.StockMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockApiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "movementDate", ignore = true)
    @Mapping(target = "totalCost", ignore = true)
    @Mapping(target = "supplierOrderId", source = "orderId")
    StockMovement toDomain(CreateStockMovementRequest dto);

    @Mapping(target = "orderId", source = "supplierOrderId")
    @Mapping(target = "createdAt", source = "movementDate")
    StockMovementDTO toDto(StockMovement movement);
}
