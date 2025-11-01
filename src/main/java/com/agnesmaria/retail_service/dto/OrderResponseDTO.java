package com.agnesmaria.retail_service.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private Long id;
    private String orderNumber;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;

    private Long customerId;
    private String customerName;
    private String customerEmail;

    private List<OrderItemResponseDTO> items;
}
