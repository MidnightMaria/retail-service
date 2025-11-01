package com.agnesmaria.retail_service.repository;

import com.agnesmaria.retail_service.model.RetailWarehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RetailWarehouseRepository extends JpaRepository<RetailWarehouse, Long> {
    Optional<RetailWarehouse> findByCode(String code);
    boolean existsByCode(String code);
}
