package com.agnesmaria.retail_service.service;

import com.agnesmaria.retail_service.client.InventoryClient;
import com.agnesmaria.retail_service.model.*;
import com.agnesmaria.retail_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InventoryClient inventoryClient;

    @Transactional
    public Order createOrder(Order order, Long warehouseId) {
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.calculateTotal();
        order.setStatus("COMPLETED");

        Order savedOrder = orderRepository.save(order);

        // 🔗 Reduce stock for each ordered product
        order.getItems().forEach(item -> {
            inventoryClient.reduceStock(
                    item.getProduct().getSku(),
                    warehouseId,
                    item.getQuantity(),
                    savedOrder.getOrderNumber()
            );
        });

        log.info("🧾 Order {} created successfully with total {}", savedOrder.getOrderNumber(), savedOrder.getTotalAmount());
        return savedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
