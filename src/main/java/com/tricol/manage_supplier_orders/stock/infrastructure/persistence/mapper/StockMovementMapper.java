package com.tricol.manage_supplier_orders.stock.infrastructure.persistence.mapper;


import com.tricol.manage_supplier_orders.stock.domain.model.StockMovement;
import com.tricol.manage_supplier_orders.stock.infrastructure.persistence.entity.StockMovementJpaEntity;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface StockMovementMapper {
    StockMovementJpaEntity toEntity(StockMovement domain);
    StockMovement toDomain(StockMovementJpaEntity entity);
}