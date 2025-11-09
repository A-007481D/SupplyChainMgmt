package com.tricol.manage_supplier_orders.stock.infrastructure.adapter;


import com.tricol.manage_supplier_orders.stock.domain.enums.MovementType;
import com.tricol.manage_supplier_orders.stock.domain.model.StockMovement;
import com.tricol.manage_supplier_orders.stock.domain.ports.StockMovementRepository;
import com.tricol.manage_supplier_orders.stock.infrastructure.persistence.entity.StockMovementJpaEntity;
import com.tricol.manage_supplier_orders.stock.infrastructure.persistence.mapper.StockMovementMapper;
import com.tricol.manage_supplier_orders.stock.infrastructure.persistence.repository.StockMovementJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class StockMovementRepositoryAdapter implements StockMovementRepository {


    private final StockMovementJpaRepository jpa;
    private final StockMovementMapper mapper;


    public StockMovementRepositoryAdapter(StockMovementJpaRepository jpa, StockMovementMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }


    @Override
    public StockMovement save(StockMovement movement) {
        StockMovementJpaEntity entity = mapper.toEntity(movement);
        StockMovementJpaEntity saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }


    @Override
    public Optional<StockMovement> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }


    @Override
    public Page<StockMovement> findAll(Pageable pageable) {
        return jpa.findAll(pageable).map(mapper::toDomain);
    }


    @Override
    public Page<StockMovement> findByProductId(Long productId, Pageable pageable) {
        return jpa.findByProductId(productId, pageable).map(mapper::toDomain);
    }


    @Override
    public Page<StockMovement> findBySupplierOrderId(Long orderId, Pageable pageable) {
        return jpa.findBySupplierOrderId(orderId, pageable).map(mapper::toDomain);
    }
    
    @Override
    public List<StockMovement> findByProductIdAndTypeOrderByMovementDateAsc(Long productId, MovementType type) {
        return jpa.findByProductIdAndTypeOrderByMovementDateAsc(productId, type)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}