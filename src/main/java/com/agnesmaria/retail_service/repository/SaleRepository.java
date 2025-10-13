package com.agnesmaria.retail_service.repository;

import com.agnesmaria.retail_service.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}
