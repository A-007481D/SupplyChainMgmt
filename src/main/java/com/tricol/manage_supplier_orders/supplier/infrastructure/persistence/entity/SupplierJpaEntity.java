package com.tricol.manage_supplier_orders.supplier.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "supplier")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String company;

    private String address;
    private String contact;

    @Column(nullable = false)
    private String email;

    private String phone;
    private String city;
    private String ice;

    @Version
    private Long version;
}