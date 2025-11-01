package com.agnesmaria.retail_service.repository;

import com.agnesmaria.retail_service.model.RetailStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RetailStockRepository extends JpaRepository<RetailStock, Long> {
    Optional<RetailStock> findByWarehouseIdAndProductId(Long warehouseId, Long productId);
    boolean existsByWarehouseIdAndProductId(Long warehouseId, Long productId);
}
