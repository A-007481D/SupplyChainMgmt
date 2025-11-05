package com.tricol.manage_supplier_orders.order.infrastructure.persistence.mapper;

import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrder;
import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrderItem;
import com.tricol.manage_supplier_orders.order.infrastructure.persistence.entity.SupplierOrderItemJpaEntity;
import com.tricol.manage_supplier_orders.order.infrastructure.persistence.entity.SupplierOrderJpaEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SupplierOrderJpaMapper {

    @Mapping(target = "items", ignore = true)
    SupplierOrder toDomain(SupplierOrderJpaEntity entity);

    @Mapping(target = "items", ignore = true)
    SupplierOrderJpaEntity toEntity(SupplierOrder order);

    @AfterMapping
    default void mapItems(SupplierOrder order, @MappingTarget SupplierOrderJpaEntity entity) {
        entity.getItems().clear();
        if (order.getItems() != null) {
            for (SupplierOrderItem item : order.getItems()) {
                SupplierOrderItemJpaEntity itemEntity = toItemEntity(item);
                itemEntity.setSupplierOrder(entity);
                entity.getItems().add(itemEntity);
            }
        }
    }

    @AfterMapping
    default void mapItemsToDomain(SupplierOrderJpaEntity entity, @MappingTarget SupplierOrder order) {
        order.getItems().clear();
        if (entity.getItems() != null) {
            for (SupplierOrderItemJpaEntity itemEntity : entity.getItems()) {
                SupplierOrderItem item = toItemDomain(itemEntity);
                order.getItems().add(item);
            }
        }
    }

    SupplierOrderItem toItemDomain(SupplierOrderItemJpaEntity entity);
    SupplierOrderItemJpaEntity toItemEntity(SupplierOrderItem item);
}
