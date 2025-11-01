package com.agnesmaria.retail_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RetailStockSetRequest {

    @NotNull
    private Long productId;

    @NotNull
    @Min(0)
    private Integer quantity; // jumlah stok yang ingin di-set atau replace
}
