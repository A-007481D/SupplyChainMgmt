package com.tricol.manage_supplier_orders.stock.api.controller;

import com.tricol.manage_supplier_orders.stock.api.dto.*;
import com.tricol.manage_supplier_orders.stock.api.mapper.StockApiMapper;
import com.tricol.manage_supplier_orders.stock.application.ports.StockServicePort;
import com.tricol.manage_supplier_orders.stock.domain.enums.MovementType;
import com.tricol.manage_supplier_orders.stock.domain.model.StockMovement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockServicePort service;
    private final StockApiMapper mapper;

    public StockController(StockServicePort service, StockApiMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping("/movements")
    public ResponseEntity<StockMovementDTO> createMovement(@Valid @RequestBody CreateStockMovementRequest req) {
        StockMovement domain = mapper.toDomain(req);
        StockMovement saved = service.recordMovement(domain.getProductId(), domain.getType(), domain.getQuantity(), domain.getUnitCost(), domain.getSupplierOrderId());
        return ResponseEntity.ok(mapper.toDto(saved));
    }

    @GetMapping("/movements")
    public ResponseEntity<Page<StockMovementDTO>> listMovements(Pageable pageable) {
        Page<StockMovementDTO> page = service.listMovements(pageable).map(mapper::toDto);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/products/{productId}/history")
    public ResponseEntity<List<StockMovementDTO>> productHistory(@PathVariable Long productId) {
        List<StockMovementDTO> list = service.listByProduct(productId, Pageable.unpaged()).getContent().stream().map(mapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/orders/deliver")
    public ResponseEntity<Void> deliverOrder(@Valid @RequestBody OrderDeliveryRequest req) {
        // map DTOs to domain StockMovement items (ENTRY)
        List<StockMovement> items = req.getItems().stream().map(i ->
                StockMovement.builder()
                        .productId(i.getProductId())
                        .quantity(i.getQuantity())
                        .unitCost(i.getUnitCost())
                        .supplierOrderId(req.getOrderId())
                        .type(MovementType.ENTRY)
                        .movementDate(OffsetDateTime.now())
                        .build()
        ).collect(Collectors.toList());

        service.handleOrderDelivery(req.getOrderId(), items);
        return ResponseEntity.noContent().build();
    }
}
