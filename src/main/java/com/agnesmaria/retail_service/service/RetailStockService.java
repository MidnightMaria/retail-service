package com.agnesmaria.retail_service.service;

import com.agnesmaria.retail_service.dto.RetailStockAdjustRequest;
import com.agnesmaria.retail_service.dto.RetailStockSetRequest;
import com.agnesmaria.retail_service.model.Product;
import com.agnesmaria.retail_service.model.RetailStock;
import com.agnesmaria.retail_service.model.RetailWarehouse;
import com.agnesmaria.retail_service.repository.ProductRepository;
import com.agnesmaria.retail_service.repository.RetailStockRepository;
import com.agnesmaria.retail_service.repository.RetailWarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RetailStockService {

    private final RetailStockRepository stockRepository;
    private final RetailWarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    private RetailWarehouse getWarehouseOr404(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Retail warehouse not found"));
    }

    private Product getProductOr404(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    private RetailStock getOrCreate(Long warehouseId, Long productId) {
        return stockRepository.findByWarehouseIdAndProductId(warehouseId, productId)
                .orElseGet(() -> {
                    RetailWarehouse wh = getWarehouseOr404(warehouseId);
                    Product p = getProductOr404(productId);
                    return RetailStock.builder()
                            .warehouse(wh)
                            .product(p)
                            .quantity(0)
                            .build();
                });
    }

    @Transactional
    public RetailStock setQuantity(Long warehouseId, RetailStockSetRequest req) {
        RetailStock stock = getOrCreate(warehouseId, req.getProductId());
        stock.setQuantity(req.getQuantity());
        return stockRepository.save(stock);
    }

    @Transactional
    public RetailStock increase(Long warehouseId, RetailStockAdjustRequest req) {
        RetailStock stock = getOrCreate(warehouseId, req.getProductId());
        stock.setQuantity(stock.getQuantity() + req.getQuantity());
        return stockRepository.save(stock);
    }

    @Transactional
    public RetailStock decrease(Long warehouseId, RetailStockAdjustRequest req) {
        RetailStock stock = getOrCreate(warehouseId, req.getProductId());
        int after = stock.getQuantity() - req.getQuantity();
        if (after < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient retail stock");
        }
        stock.setQuantity(after);
        return stockRepository.save(stock);
    }

    public RetailStock get(Long warehouseId, Long productId) {
        return stockRepository.findByWarehouseIdAndProductId(warehouseId, productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Retail stock not found"));
    }
}
