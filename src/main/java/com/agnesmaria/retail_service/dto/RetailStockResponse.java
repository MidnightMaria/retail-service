package com.agnesmaria.retail_service.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetailStockResponse {
    private Long id;
    private Long warehouseId;
    private Long productId;
    private String productSku;
    private String productName;
    private int quantity;
}
