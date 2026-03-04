package com.agnesmaria.retail_service.service;

import com.agnesmaria.retail_service.client.InventoryClient;
import com.agnesmaria.retail_service.dto.*;
import com.agnesmaria.retail_service.model.Product;
import com.agnesmaria.retail_service.model.RetailStock;
import com.agnesmaria.retail_service.model.RetailWarehouse;
import com.agnesmaria.retail_service.repository.ProductRepository;
import com.agnesmaria.retail_service.repository.RetailStockRepository;
import com.agnesmaria.retail_service.repository.RetailWarehouseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RetailStockService {

    private final RetailStockRepository stockRepository;
    private final RetailWarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final InventoryClient inventoryClient;

    // Ambil stok berdasarkan warehouse dan product
    public RetailStock get(Long warehouseId, Long productId) {
        return stockRepository.findByWarehouseIdAndProductId(warehouseId, productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found"));
    }

    // Set/replace stok — sekarang mendukung productId atau productSku
    @Transactional
    public RetailStock setQuantity(Long warehouseId, RetailStockSetRequest req) {
        RetailWarehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found"));

        Product product = resolveProduct(req);

        RetailStock stock = stockRepository.findByWarehouseIdAndProductId(warehouseId, product.getId())
                .orElse(new RetailStock(null, warehouse, product, 0, null));

        stock.setQuantity(req.getQuantity());
        RetailStock saved = stockRepository.save(stock);

        log.info("Stock set: {} units of {} (productId={}) at warehouse {}",
                req.getQuantity(), product.getSku(), product.getId(), warehouseId);

        return saved;
    }

    // Tambah stok
    @Transactional
    public RetailStock increase(Long warehouseId, RetailStockAdjustRequest req) {
        RetailStock stock = stockRepository.findByWarehouseIdAndProductId(warehouseId, req.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found"));

        stock.setQuantity(stock.getQuantity() + req.getQuantity());
        RetailStock saved = stockRepository.save(stock);

        log.info("Increased stock: +{} units (warehouse={}, productId={})",
                req.getQuantity(), warehouseId, req.getProductId());

        return saved;
    }

    //Kurangi stok
    @Transactional
    public RetailStock decrease(Long warehouseId, RetailStockAdjustRequest req) {
        RetailStock stock = stockRepository.findByWarehouseIdAndProductId(warehouseId, req.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found"));

        if (stock.getQuantity() < req.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock");
        }

        stock.setQuantity(stock.getQuantity() - req.getQuantity());
        RetailStock saved = stockRepository.save(stock);

        log.info("Decreased stock: -{} units (warehouse={}, productId={})",
                req.getQuantity(), warehouseId, req.getProductId());

        return saved;
    }

    //Request restock ke inventory-service + update stok lokal retail
    @Transactional
    public void requestRestock(RestockRequest request) {
        try {
            String reference = "RESTOCK-" + UUID.randomUUID();

            log.info("Requesting transfer: {} units of {} (from warehouse {} → retail {})",
                    request.getQuantity(), request.getProductSku(),
                    request.getFromWarehouseId(), request.getToWarehouseId());

            //Panggil Inventory Service untuk transfer stok pusat
            inventoryClient.transferStock(
                    request.getFromWarehouseId(),
                    request.getToWarehouseId(),
                    request.getProductSku(),
                    request.getQuantity(),
                    reference
            );

            log.info("Transfer success on inventory-service. Syncing retail stock...");

            // Update stok lokal di retail DB
            Product product = productRepository.findBySku(request.getProductSku())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

            RetailWarehouse warehouse = warehouseRepository.findById(request.getToWarehouseId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Retail warehouse not found"));

            RetailStock stock = stockRepository
                    .findByWarehouseIdAndProductId(warehouse.getId(), product.getId())
                    .orElse(new RetailStock(null, warehouse, product, 0, null));

            stock.setQuantity(stock.getQuantity() + request.getQuantity());
            stockRepository.save(stock);

            log.info("Retail stock updated successfully → +{} units of {} at warehouse {}",
                    request.getQuantity(), request.getProductSku(), warehouse.getId());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Restock failed: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Failed to restock from central warehouse: " + e.getMessage());
        }
    }

    //Helper untuk resolve product dari request
    private Product resolveProduct(RetailStockSetRequest req) {
        if (req.getProductId() != null) {
            return productRepository.findById(req.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found by ID"));
        } else if (req.getProductSku() != null) {
            return productRepository.findBySku(req.getProductSku())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found by SKU"));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product identifier (ID or SKU) is required");
        }
    }
}
