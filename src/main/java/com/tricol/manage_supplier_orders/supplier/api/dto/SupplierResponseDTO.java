package com.tricol.manage_supplier_orders.supplier.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierResponseDTO {
    private Long id;
    private String email;
    private String phone;
    private String address;
    private String company;
    private String contact;
    private String ice;
    private String city;
}
