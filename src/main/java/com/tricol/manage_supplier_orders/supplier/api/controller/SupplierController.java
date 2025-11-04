package com.tricol.manage_supplier_orders.supplier.api.controller;

import com.tricol.manage_supplier_orders.supplier.api.dto.SupplierRequestDTO;
import com.tricol.manage_supplier_orders.supplier.api.dto.SupplierResponseDTO;
import com.tricol.manage_supplier_orders.supplier.api.mapper.SupplierApiMapper;
import com.tricol.manage_supplier_orders.supplier.application.ports.SupplierServicePort;
import com.tricol.manage_supplier_orders.supplier.application.ports.SupplierServicePort.CreateSupplierCommand;
import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
    @RequestMapping("/api/v1/suppliers")
@Validated
public class SupplierController {

    private final SupplierServicePort supplierServicePort;
    private final SupplierApiMapper apiMapper;

    public SupplierController(SupplierServicePort supplierServicePort, SupplierApiMapper apiMapper) {
        this.supplierServicePort = supplierServicePort;
        this.apiMapper = apiMapper;
    }

    @PostMapping
    public ResponseEntity<SupplierResponseDTO> create(@Valid @RequestBody SupplierRequestDTO request) {
        CreateSupplierCommand cmd = new CreateSupplierCommand(
                request.getCompany(),
                request.getAddress(),
                request.getContact(),
                request.getEmail(),
                request.getPhone(),
                request.getCity(),
                request.getIce()
        );
        Supplier savedSupplier = supplierServicePort.create(cmd);
        return ResponseEntity.ok(apiMapper.toDto(savedSupplier));
    }

    @GetMapping
    public Page<SupplierResponseDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable p = PageRequest.of(page, size);
        Page<Supplier> supplierPage = supplierServicePort.list(p);
        return supplierPage.map(apiMapper::toDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> getById(@PathVariable Long id) {
        Supplier supplier =supplierServicePort.getById(id);
        return ResponseEntity.ok(apiMapper.toDto(supplier));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> update(@PathVariable Long id,
                                                      @Valid @RequestBody SupplierRequestDTO request) {
        CreateSupplierCommand cmd = new CreateSupplierCommand(
                request.getCompany(),
                request.getAddress(),
                request.getContact(),
                request.getEmail(),
                request.getPhone(),
                request.getCity(),
                request.getIce()
        );
        Supplier updatedSupplier = supplierServicePort.update(id, cmd);
        return ResponseEntity.ok(apiMapper.toDto(updatedSupplier));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supplierServicePort.delete(id);
        return ResponseEntity.noContent().build();
    }
}
