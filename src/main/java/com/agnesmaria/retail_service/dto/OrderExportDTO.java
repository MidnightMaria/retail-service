package com.agnesmaria.retail_service.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderExportDTO {
    private String orderNumber;
    private LocalDateTime orderDate;
    private String productSku;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    private String warehouseCode;
    private String customerName;
}
