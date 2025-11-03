package com.tricol.manage_supplier_orders.product.api.mapper;

import com.tricol.manage_supplier_orders.product.api.dto.ProductResponseDTO;
import com.tricol.manage_supplier_orders.product.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductApiMapper {

    ProductApiMapper INSTANCE = Mappers.getMapper(ProductApiMapper.class);

    ProductResponseDTO toDto(Product product);

    Product toDomain(ProductResponseDTO productResponseDTO);
}
