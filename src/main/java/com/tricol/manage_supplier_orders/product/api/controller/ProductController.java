package com.tricol.manage_supplier_orders.product.api.controller;

import com.tricol.manage_supplier_orders.product.api.dto.ProductResponseDTO;
import com.tricol.manage_supplier_orders.product.api.mapper.ProductApiMapper;
import com.tricol.manage_supplier_orders.product.application.ports.ProductServicePort;
import com.tricol.manage_supplier_orders.product.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductServicePort productService;
    private final ProductApiMapper mapper;

    public ProductController(ProductServicePort productService, ProductApiMapper mapper) {
        this.productService = productService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductResponseDTO dto) {
        Product saved = productService.createProduct(mapper.toDomain(dto));
        return ResponseEntity.ok(mapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @RequestBody ProductResponseDTO dto) {
        Product updated = productService.updateProduct(id, mapper.toDomain(dto));
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(mapper.toDto(product));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(Pageable pageable) {
        Page<ProductResponseDTO> page = productService.getAllProducts(pageable)
                .map(mapper::toDto);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDTO>> searchProducts(@RequestParam String name, Pageable pageable) {
        Page<ProductResponseDTO> page = productService.searchProductsByName(name, pageable)
                .map(mapper::toDto);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<ProductResponseDTO>> getBySupplier(@PathVariable Long supplierId) {
        List<ProductResponseDTO> list = productService.getProductsBySupplier(supplierId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponseDTO>> getLowStock(@RequestParam Integer threshold) {
        List<ProductResponseDTO> list = productService.getLowStockProducts(threshold)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
