package com.agnesmaria.retail_service.service;

import com.agnesmaria.retail_service.model.Sale;
import com.agnesmaria.retail_service.repository.SaleRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SalesService {
    private final SaleRepository saleRepository;

    public SalesService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    public Sale recordSale(Sale sale) {
        return saleRepository.save(sale);
    }
}
