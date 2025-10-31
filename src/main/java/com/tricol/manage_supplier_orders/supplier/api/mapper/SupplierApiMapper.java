package com.tricol.manage_supplier_orders.supplier.api.mapper;

import com.tricol.manage_supplier_orders.supplier.api.dto.SupplierRequestDTO;
import com.tricol.manage_supplier_orders.supplier.api.dto.SupplierResponseDTO;
import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupplierApiMapper {

    Supplier toDomain(SupplierRequestDTO request);

    SupplierResponseDTO toDto(Supplier domain);
}
