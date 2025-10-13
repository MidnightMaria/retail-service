package com.agnesmaria.retail_service.controller;

import com.agnesmaria.retail_service.model.Sale;
import com.agnesmaria.retail_service.service.SalesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SalesController {

    private final SalesService salesService;
    private final WebClient webClient;

    @Value("${inventory.api.url}")
    private String inventoryApiUrl;

    public SalesController(SalesService salesService, WebClient.Builder webClientBuilder) {
        this.salesService = salesService;
        this.webClient = webClientBuilder.build();
    }

    @GetMapping
    public List<Sale> getAllSales() {
        return salesService.getAllSales();
    }

    @PostMapping
    public Sale createSale(@RequestBody Sale sale) {
        // ambil produk dari IMS
        var product = webClient.get()
                .uri(inventoryApiUrl + "/" + sale.getProductId())
                .retrieve()
                .bodyToMono(com.agnesmaria.retail_service.model.Product.class)
                .block();

        if (product == null || product.getStock() < sale.getQuantity()) {
            throw new RuntimeException("Stok tidak cukup atau produk tidak ditemukan di Inventory Service");
        }

        // kurangi stok
        product.setStock(product.getStock() - sale.getQuantity());
        webClient.put()
                .uri(inventoryApiUrl + "/" + product.getId())
                .body(Mono.just(product), com.agnesmaria.retail_service.model.Product.class)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        sale.setPrice(product.getPrice());
        return salesService.recordSale(sale);
    }
}
