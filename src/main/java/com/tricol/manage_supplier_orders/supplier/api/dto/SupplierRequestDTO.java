package com.tricol.manage_supplier_orders.supplier.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierRequestDTO {

    @NotBlank
    private String company;
    private String address;
    private String contact;
    @Email
    private String email;
    private String phone;
    private String city;
    private String ice;
}
