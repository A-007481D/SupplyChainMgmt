package com.tricol.manage_supplier_orders.product.infrastructure.persistence.mapper;

import com.tricol.manage_supplier_orders.product.domain.model.Product;
import com.tricol.manage_supplier_orders.product.infrastructure.persistence.entity.ProductJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductJpaMapper {
    ProductJpaEntity toEntity(Product product);

    Product toDomain(ProductJpaEntity entity);

}
