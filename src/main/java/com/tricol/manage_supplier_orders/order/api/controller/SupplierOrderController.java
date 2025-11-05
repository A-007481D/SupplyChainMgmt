package com.tricol.manage_supplier_orders.order.api.controller;

import com.tricol.manage_supplier_orders.order.api.dto.CreateSupplierOrderRequest;
import com.tricol.manage_supplier_orders.order.api.dto.SupplierOrderDTO;
import com.tricol.manage_supplier_orders.order.api.mapper.SupplierOrderApiMapper;
import com.tricol.manage_supplier_orders.order.application.service.SupplierOrderService;
import com.tricol.manage_supplier_orders.order.domain.model.SupplierOrder;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/orders")
public class SupplierOrderController {
    private final SupplierOrderService service;
    private final SupplierOrderApiMapper apiMapper;


    public SupplierOrderController(SupplierOrderService service, SupplierOrderApiMapper apiMapper) {
        this.service = service;
        this.apiMapper = apiMapper;
    }

    @PostMapping
    public ResponseEntity<SupplierOrderDTO> createSupplierOrder(@Valid @RequestBody CreateSupplierOrderRequest req) {
        SupplierOrder domain = apiMapper.toDomain(req);
        SupplierOrder saved = service.createOrder(domain);
        return ResponseEntity.ok(apiMapper.toDto(saved));
    }





}
