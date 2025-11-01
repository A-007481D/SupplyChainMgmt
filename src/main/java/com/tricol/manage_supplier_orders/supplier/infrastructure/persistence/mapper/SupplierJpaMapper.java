package com.tricol.manage_supplier_orders.supplier.infrastructure.persistence.mapper;

import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import com.tricol.manage_supplier_orders.supplier.infrastructure.persistence.entity.SupplierJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface SupplierJpaMapper {
//    SupplierJpaMapper INSTANCE = Mappers.getMapper(SupplierJpaMapper.class);

    SupplierJpaEntity toEntity(Supplier supplier);
    Supplier toDomain(SupplierJpaEntity entity);
}
