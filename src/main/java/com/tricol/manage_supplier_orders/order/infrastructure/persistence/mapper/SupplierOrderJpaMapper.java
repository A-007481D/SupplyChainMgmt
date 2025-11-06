package com.tricol.manage_supplier_orders.order.infrastructure.persistence.mapper;

import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrder;
import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrderItem;
import com.tricol.manage_supplier_orders.order.infrastructure.persistence.entity.SupplierOrderItemJpaEntity;
import com.tricol.manage_supplier_orders.order.infrastructure.persistence.entity.SupplierOrderJpaEntity;
import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import org.mapstruct.*;

import java.util.ArrayList;

@Mapper(componentModel = "spring")
public interface SupplierOrderJpaMapper {

    @Mapping(target = "items", ignore = true)
    @Mapping(target = "supplier", source = "supplierId", qualifiedByName = "mapSupplierIdToSupplier")
    SupplierOrder toDomain(SupplierOrderJpaEntity entity);

    @Mapping(target = "items", ignore = true)
    @Mapping(target = "supplierId", source = "supplier.id")
    SupplierOrderJpaEntity toEntity(SupplierOrder order);

    @AfterMapping
    default void mapItems(SupplierOrder order, @MappingTarget SupplierOrderJpaEntity entity) {
        // Initialize items collection if null
        if (entity.getItems() == null) {
            entity.setItems(new ArrayList<>());
        } else {
            entity.getItems().clear();
        }
        
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
    
    @Named("mapSupplierIdToSupplier")
    default Supplier mapSupplierIdToSupplier(Long supplierId) {
        if (supplierId == null) {
            return null;
        }
        Supplier supplier = new Supplier();
        supplier.setId(supplierId);
        return supplier;
    }
}
