package com.agnesmaria.retail_service.controller;

import com.agnesmaria.retail_service.model.Order;
import com.agnesmaria.retail_service.model.Product;
import com.agnesmaria.retail_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

    @Value("${inventory.api.url}")
    private String inventoryApiUrl;

    public OrderController(OrderRepository orderRepository, WebClient.Builder webClientBuilder) {
        this.orderRepository = orderRepository;
        this.webClient = webClientBuilder.build();
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        // 1️⃣ Ambil data produk dari Inventory Service
        Product product = webClient.get()
                .uri(inventoryApiUrl + "/" + order.getProductId())
                .retrieve()
                .bodyToMono(Product.class)
                .block();

        if (product == null || product.getStock() < order.getQuantity()) {
            throw new RuntimeException("Stok tidak cukup atau produk tidak ditemukan.");
        }

        // 2️⃣ Hitung total harga
        double totalPrice = order.getQuantity() * product.getPrice();
        order.setTotalPrice(totalPrice);

        // 3️⃣ Kurangi stok produk di Inventory
        product.setStock(product.getStock() - order.getQuantity());
        webClient.put()
                .uri(inventoryApiUrl + "/" + product.getId())
                .body(Mono.just(product), Product.class)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        // 4️⃣ Simpan order di Retail Service (in-memory)
        return orderRepository.save(order);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
