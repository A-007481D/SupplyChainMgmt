package com.tricol.manage_supplier_orders.order.infrastructure.persistence.mapper;

import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrder;
import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrderItem;
import com.tricol.manage_supplier_orders.order.infrastructure.persistence.entity.SupplierOrderItemJpaEntity;
import com.tricol.manage_supplier_orders.order.infrastructure.persistence.entity.SupplierOrderJpaEntity;
import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface SupplierOrderJpaMapper {

    @Mapping(target = "items", ignore = true)
    @Mapping(target = "supplier", source = "supplierId", qualifiedByName = "mapSupplierIdToSupplier")
    SupplierOrder toDomain(SupplierOrderJpaEntity entity);

    @Mapping(target = "items", ignore = true)
    @Mapping(target = "supplierId", source = "supplier.id")
    SupplierOrderJpaEntity toEntity(SupplierOrder order);
    
    @Mapping(target = "supplierOrder", ignore = true)
    SupplierOrderItemJpaEntity toItemEntity(SupplierOrderItem item);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "unitPrice", source = "unitPrice")
    @Mapping(target = "subtotal", expression = "java(entity.getUnitPrice().multiply(new java.math.BigDecimal(entity.getQuantity())))")
    SupplierOrderItem toItemDomain(SupplierOrderItemJpaEntity entity);
    
    @AfterMapping
    default void mapItems(SupplierOrder order, @MappingTarget SupplierOrderJpaEntity entity) {
        if (entity.getItems() == null) {
            entity.setItems(new ArrayList<>());
        } else {
            entity.getItems().clear();
        }
        
        if (order.getItems() != null) {
            for (SupplierOrderItem item : order.getItems()) {
                if (item != null) {
                    SupplierOrderItemJpaEntity itemEntity = toItemEntity(item);
                    if (itemEntity != null) {
                        itemEntity.setSupplierOrder(entity);
                        entity.getItems().add(itemEntity);
                    }
                }
            }
        }
    }
    
    @AfterMapping
    default void mapItemsToDomain(SupplierOrderJpaEntity entity, @MappingTarget SupplierOrder order) {
        if (order.getItems() == null) {
            order.setItems(new ArrayList<>());
        } else {
            order.getItems().clear();
        }
        
        if (entity.getItems() != null) {
            for (SupplierOrderItemJpaEntity itemEntity : entity.getItems()) {
                if (itemEntity != null) {
                    SupplierOrderItem item = toItemDomain(itemEntity);
                    if (item != null) {
                        item.setOrder(order);
                        order.getItems().add(item);
                    }
                }
            }
        }
    }
    
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
