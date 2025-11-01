package com.agnesmaria.retail_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    private String customerName;
    private String customerEmail;

    @CreationTimestamp
    private LocalDateTime orderDate;

    private String status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("orderItems") 
    private List<OrderItem> items;

    private BigDecimal totalAmount;

    public void calculateTotal() {
        if (items != null && !items.isEmpty()) {
            totalAmount = items.stream()
                    .map(OrderItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            totalAmount = BigDecimal.ZERO;
        }
    }
}
