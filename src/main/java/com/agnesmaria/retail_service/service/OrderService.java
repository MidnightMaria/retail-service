package com.agnesmaria.retail_service.service;

import com.agnesmaria.retail_service.dto.OrderItemResponseDTO;
import com.agnesmaria.retail_service.dto.OrderResponseDTO;
import com.agnesmaria.retail_service.dto.RetailStockAdjustRequest;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final RetailStockService retailStockService;

    @Transactional
    public OrderResponseDTO createOrder(Order order, Long warehouseId) {
        // 🧾 Generate order number
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setStatus("COMPLETED");

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must contain at least one item");
        }

        // 🧍 Link customer snapshot
        if (order.getCustomer() != null && order.getCustomer().getId() != null) {
            Customer customer = customerRepository.findById(order.getCustomer().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Customer not found with ID: " + order.getCustomer().getId()));
            order.setCustomer(customer);
            order.setCustomerName(customer.getName());
            order.setCustomerEmail(customer.getEmail());
        }

        // 🧩 Link item & price
        for (OrderItem item : order.getItems()) {
            if (item.getProduct() == null || item.getProduct().getId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product ID is required for each order item");
            }

            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Product not found with ID: " + item.getProduct().getId()));

            if (item.getUnitPrice() == null) {
                item.setUnitPrice(product.getPrice());
            }

            item.setOrder(order);
            item.setProduct(product);
        }

        // 💾 Simpan order
        order.calculateTotal();
        Order savedOrder = orderRepository.save(order);

        // 📦 Kurangi stok retail
        for (OrderItem item : savedOrder.getItems()) {
            try {
                RetailStockAdjustRequest stockRequest = new RetailStockAdjustRequest();
                stockRequest.setProductId(item.getProduct().getId());
                stockRequest.setQuantity(item.getQuantity());
                retailStockService.decrease(warehouseId, stockRequest);
                log.info("✅ Retail stock reduced for Product ID {} by {}", item.getProduct().getId(), item.getQuantity());
            } catch (ResponseStatusException e) {
                log.error("⚠️ Failed to reduce retail stock for Product ID {}: {}", item.getProduct().getId(), e.getReason());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Insufficient stock for Product ID: " + item.getProduct().getId());
            }
        }

        log.info("🧾 Order {} created successfully", savedOrder.getOrderNumber());
        return mapToResponseDTO(savedOrder);
    }

    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        return mapToResponseDTO(order);
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        orderRepository.deleteById(id);
        log.info("🗑️ Order with ID {} deleted successfully", id);
    }

    // ✨ Mapping helper
    private OrderResponseDTO mapToResponseDTO(Order order) {
        List<OrderItemResponseDTO> items = order.getItems().stream()
                .map(item -> {
                    Product product = item.getProduct();
                    return OrderItemResponseDTO.builder()
                            .id(item.getId())
                            .productId(product != null ? product.getId() : null)
                            .productName(product != null ? product.getName() : null)
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .subtotal(item.getSubtotal())
                            .build();
                })
                .collect(Collectors.toList());

        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .items(items)
                .build();
    }
}
