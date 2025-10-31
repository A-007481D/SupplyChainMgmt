package com.tricol.manage_supplier_orders.supplier.domain.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    private Long id;
    private String company;
    private String address;
    private String contact;
    private String email;
    private String phone;
    private String city;
    private String ice;


}


