package com.agnesmaria.retail_service.seed;

import com.agnesmaria.retail_service.model.*;
import com.agnesmaria.retail_service.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepo;
    private final CustomerRepository customerRepo;
    private final OrderRepository orderRepo;
    private final RetailWarehouseRepository warehouseRepo;
    private final OrderItemRepository orderItemRepo;

    @Value("${app.seed-data:true}")
    private boolean seedDataEnabled;

    public DataSeeder(ProductRepository productRepo,
                      CustomerRepository customerRepo,
                      OrderRepository orderRepo,
                      RetailWarehouseRepository warehouseRepo,
                      OrderItemRepository orderItemRepo) {
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
        this.orderRepo = orderRepo;
        this.warehouseRepo = warehouseRepo;
        this.orderItemRepo = orderItemRepo;
    }

    @Override
    public void run(String... args) {
        if (!seedDataEnabled) {
            System.out.println("⚠️ Retail seeding skipped (app.seed-data=false)");
            return;
        }

        seedWarehouses();
        seedProducts();
        seedCustomers();
        seedOrders();
    }

    // 🏪 Retail Store Locations
    private void seedWarehouses() {
        if (warehouseRepo.count() == 0) {
            List<RetailWarehouse> warehouses = List.of(
                    RetailWarehouse.builder().code("RWH-001").name("Retail Bandung")
                            .address("Jl. Setiabudi No. 99, Bandung").active(true).build(),
                    RetailWarehouse.builder().code("RWH-002").name("Retail Jakarta")
                            .address("Jl. Sudirman No. 55, Jakarta").active(true).build(),
                    RetailWarehouse.builder().code("RWH-003").name("Retail Surabaya")
                            .address("Jl. Darmo No. 23, Surabaya").active(true).build()
            );
            warehouseRepo.saveAll(warehouses);
            System.out.println("🏬 Retail warehouses seeded (" + warehouses.size() + ")");
        }
    }

    // 🧾 Product Catalog
    private void seedProducts() {
        if (productRepo.count() == 0) {
            List<Product> products = List.of(
                    Product.builder().sku("PROD-001").name("Laptop ASUS ROG").description("High performance gaming laptop")
                            .price(BigDecimal.valueOf(20000000)).stock(10).active(true).build(),
                    Product.builder().sku("PROD-002").name("Monitor LG UltraGear 27\"").description("165Hz QHD gaming monitor")
                            .price(BigDecimal.valueOf(4500000)).stock(30).active(true).build(),
                    Product.builder().sku("PROD-003").name("Keyboard Logitech G Pro X").description("Hot-swap mechanical keyboard")
                            .price(BigDecimal.valueOf(1800000)).stock(50).active(true).build(),
                    Product.builder().sku("PROD-004").name("Mouse Razer Viper Mini").description("Ultra lightweight gaming mouse")
                            .price(BigDecimal.valueOf(800000)).stock(70).active(true).build(),
                    Product.builder().sku("PROD-005").name("Headset HyperX Cloud II").description("7.1 surround sound headset")
                            .price(BigDecimal.valueOf(1500000)).stock(60).active(true).build(),
                    Product.builder().sku("PROD-006").name("SSD Samsung 980 PRO 1TB").description("High-speed NVMe SSD")
                            .price(BigDecimal.valueOf(2200000)).stock(40).active(true).build(),
                    Product.builder().sku("PROD-007").name("GPU NVIDIA RTX 4070").description("Advanced graphics card")
                            .price(BigDecimal.valueOf(12000000)).stock(25).active(true).build(),
                    Product.builder().sku("PROD-008").name("Mechanical Keyboard Keychron K6").description("Wireless RGB keyboard")
                            .price(BigDecimal.valueOf(1600000)).stock(45).active(true).build(),
                    Product.builder().sku("PROD-009").name("Razer DeathAdder V3").description("Ergonomic gaming mouse")
                            .price(BigDecimal.valueOf(1300000)).stock(55).active(true).build(),
                    Product.builder().sku("PROD-010").name("Logitech MX Master 3S").description("Wireless productivity mouse")
                            .price(BigDecimal.valueOf(1800000)).stock(35).active(true).build()
            );
            productRepo.saveAll(products);
            System.out.println("🧾 Retail products seeded (" + products.size() + ")");
        }
    }

    // 👥 Customers
    private void seedCustomers() {
        if (customerRepo.count() == 0) {
            Random random = new Random();
            List<String> cities = List.of("Bandung", "Jakarta", "Surabaya", "Yogyakarta", "Medan");
            List<String> names = List.of("Agnes Maria", "Budi Santoso", "Citra Lestari", "Daniel Hartono",
                    "Eka Putra", "Fiona Maharani", "Gilang Saputra", "Hendra Wijaya", "Intan Dewi", "Joko Prasetyo",
                    "Kevin Wijaya", "Linda Kusuma", "Maya Oktaviani", "Nina Rahma", "Oscar Pranata");

            List<Customer> customers = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                String name = names.get(random.nextInt(names.size())) + " " + (char) ('A' + random.nextInt(26));
                String city = cities.get(random.nextInt(cities.size()));

                // ✅ Ensure unique email by adding random suffix
                String uniqueEmail = name.toLowerCase().replace(" ", ".") + "." +
                        UUID.randomUUID().toString().substring(0, 5) + "@example.com";

                customers.add(Customer.builder()
                        .name(name)
                        .email(uniqueEmail)
                        .phone("0812" + (1000000 + random.nextInt(9000000)))
                        .address("Jl. " + city + " No. " + (10 + random.nextInt(90)))
                        .city(city)
                        .country("Indonesia")
                        .build());
            }
            customerRepo.saveAll(customers);
            System.out.println("👥 Retail customers seeded (" + customers.size() + ")");
        }
    }

    // 🛒 Orders (with realistic weekend sales spike)
    private void seedOrders() {
        if (orderRepo.count() > 0) {
            System.out.println("ℹ️ Orders already exist, skipping seed.");
            return;
        }

        Random random = new Random();
        List<Customer> customers = customerRepo.findAll();
        List<Product> products = productRepo.findAll();
        List<RetailWarehouse> warehouses = warehouseRepo.findAll();

        int totalOrders = 500;
        List<Order> orders = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < totalOrders; i++) {
            Customer customer = customers.get(random.nextInt(customers.size()));
            RetailWarehouse warehouse = warehouses.get(random.nextInt(warehouses.size()));
            LocalDateTime date = now.minusDays(random.nextInt(180));

            boolean isWeekend = switch (date.getDayOfWeek()) {
                case FRIDAY, SATURDAY, SUNDAY -> true;
                default -> false;
            };

            int itemCount = isWeekend ? 3 : 2;
            List<OrderItem> orderItems = new ArrayList<>();

            Order order = Order.builder()
                    .orderNumber("ORD-" + (1000 + i))
                    .customer(customer)
                    .customerName(customer.getName())
                    .customerEmail(customer.getEmail())
                    .orderDate(date)
                    .status(isWeekend ? "COMPLETED" : "PENDING")
                    .build();

            for (int j = 0; j < itemCount; j++) {
                Product product = products.get(random.nextInt(products.size()));
                int quantity = random.nextInt(3) + 1;
                BigDecimal unitPrice = product.getPrice();

                orderItems.add(OrderItem.builder()
                        .product(product)
                        .warehouse(warehouse)
                        .quantity(quantity)
                        .unitPrice(unitPrice)
                        .order(order)
                        .build());
            }

            order.setItems(orderItems);
            order.calculateTotal();
            orders.add(order);
        }

        orderRepo.saveAll(orders);
        System.out.println("🛒 Retail orders seeded (" + orders.size() + ")");
        System.out.println("✅ Retail seeding complete (ready for analytics)");
    }
}
