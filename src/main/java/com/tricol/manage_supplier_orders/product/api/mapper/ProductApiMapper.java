package com.tricol.manage_supplier_orders.product.api.mapper;

import com.tricol.manage_supplier_orders.product.api.dto.ProductRequestDTO;
import com.tricol.manage_supplier_orders.product.api.dto.ProductResponseDTO;
import com.tricol.manage_supplier_orders.product.domain.enums.Category;
import com.tricol.manage_supplier_orders.product.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProductApiMapper {

    ProductResponseDTO toDto(Product product);

    @Mapping(source = "category", target = "category", qualifiedByName = "StringToCategory")
    Product toDomain(ProductRequestDTO productRequestDTO);

    @Named("StringToCategory")
    default Category stringToCategory(String category) {
        if (category == null) return null;
        try {
            return Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid category: " + category);
        }
    }
}
