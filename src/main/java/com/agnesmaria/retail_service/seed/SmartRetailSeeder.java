package com.agnesmaria.retail_service.seed;

import com.agnesmaria.retail_service.model.*;
import com.agnesmaria.retail_service.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

// @Component
@RequiredArgsConstructor
public class SmartRetailSeeder implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final RetailWarehouseRepository retailWarehouseRepository;

    private final Random random = new SecureRandom();

    private static final String INVENTORY_API_URL =
            System.getenv().getOrDefault("INVENTORY_API_URL", "http://inventory-service:8080");

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (orderRepository.count() > 0) {
            System.out.println("SmartRetailSeeder skipped — data already exists.");
            return;
        }

        System.out.println("Running SmartRetailSeeder (auto-creating base data if missing)...");

        List<Customer> customers = seedCustomersIfEmpty();
        List<Product> products = seedProductsIfEmpty();
        List<RetailWarehouse> warehouses = seedWarehousesIfEmpty();

        if (customers.isEmpty() || products.isEmpty() || warehouses.isEmpty()) {
            System.out.println("Still missing base data — seeding aborted.");
            return;
        }

        seedOrders(customers, products, warehouses, 5000);

        System.out.println("SmartRetailSeeder finished — orders linked with warehouses.");
    }

    private List<Customer> seedCustomersIfEmpty() {
        if (customerRepository.count() > 0) return customerRepository.findAll();

        System.out.println("Seeding 50 customers...");
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            customers.add(Customer.builder()
                    .name("Customer " + (i + 1))
                    .email("customer" + (i + 1) + "@example.com")
                    .phone("0812" + (1000000 + random.nextInt(8999999)))
                    .address("Jl. Example No. " + (i + 1))
                    .city("Jakarta")
                    .country("Indonesia")
                    .createdAt(LocalDateTime.now().minusDays(random.nextInt(100)))
                    .updatedAt(LocalDateTime.now())
                    .build());
        }
        return customerRepository.saveAll(customers);
    }

    private List<RetailWarehouse> seedWarehousesIfEmpty() {
        if (retailWarehouseRepository.count() > 0) return retailWarehouseRepository.findAll();

        System.out.println("Seeding 5 retail warehouses...");
        List<RetailWarehouse> warehouses = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            warehouses.add(RetailWarehouse.builder()
                    .code("RWH-" + (i + 1))
                    .name("Retail Warehouse " + (i + 1))
                    .address("Jl. Retail " + (i + 1))
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
        }
        return retailWarehouseRepository.saveAll(warehouses);
    }

    private List<Product> seedProductsIfEmpty() {
        if (productRepository.count() > 0) return productRepository.findAll();

        System.out.println("Trying to fetch products from Inventory Service...");
        List<Product> products = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            Product[] inventoryProducts = restTemplate.getForObject(INVENTORY_API_URL + "/api/products", Product[].class);
            if (inventoryProducts != null && inventoryProducts.length > 0) {
                System.out.println("Imported " + inventoryProducts.length + " products from Inventory Service.");
                for (Product p : inventoryProducts) {
                    Product retailProduct = Product.builder()
                            .name(p.getName())
                            .sku(p.getSku())
                            .price(p.getPrice())
                            .description(p.getDescription())
                            .active(true)
                            .stock(100 + random.nextInt(400))
                            .updatedAt(LocalDateTime.now())
                            .build();
                    products.add(retailProduct);
                }
                return productRepository.saveAll(products);
            } else {
                System.out.println("Inventory API returned no data, generating local products instead.");
            }
        } catch (Exception e) {
            System.out.println("Could not connect to Inventory API — fallback to local products.");
        }

        for (int i = 0; i < 100; i++) {
            products.add(Product.builder()
                    .name("Product " + (i + 1))
                    .sku("SKU-" + (1000 + i))
                    .price(BigDecimal.valueOf(50000 + random.nextInt(2000000)))
                    .description("Generated retail product " + (i + 1))
                    .active(true)
                    .stock(50 + random.nextInt(300))
                    .updatedAt(LocalDateTime.now())
                    .build());
        }
        return productRepository.saveAll(products);
    }

    private void seedOrders(List<Customer> customers, List<Product> products,
                            List<RetailWarehouse> warehouses, int count) {

        List<Order> orders = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Customer customer = customers.get(random.nextInt(customers.size()));
            String orderNumber = "ORD-" + String.format("%05d", i + 1);
            LocalDateTime orderDate = LocalDateTime.now().minusDays(random.nextInt(365));

            int itemCount = 1 + random.nextInt(3);
            List<OrderItem> items = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;

            for (int j = 0; j < itemCount; j++) {
                Product product = products.get(random.nextInt(products.size()));
                int qty = 1 + random.nextInt(5);
                BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(qty));
                total = total.add(subtotal);

                RetailWarehouse selectedWarehouse = warehouses.get(random.nextInt(warehouses.size()));

                items.add(OrderItem.builder()
                        .product(product)
                        .warehouse(selectedWarehouse)
                        .quantity(qty)
                        .unitPrice(product.getPrice())
                        .build());
            }

            Order order = Order.builder()
                    .orderNumber(orderNumber)
                    .customer(customer)
                    .customerName(customer.getName())
                    .customerEmail(customer.getEmail())
                    .status(randomStatus())
                    .orderDate(orderDate)
                    .totalAmount(total)
                    .items(items)
                    .build();

            items.forEach(item -> item.setOrder(order));
            orders.add(order);
        }

        orderRepository.saveAll(orders);
        System.out.println("Created " + orders.size() + " orders linked with warehouses.");
    }

    private String randomStatus() {
        return List.of("COMPLETED", "PENDING", "CANCELLED", "SHIPPED")
                .get(random.nextInt(4));
    }
}
