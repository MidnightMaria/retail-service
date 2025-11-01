package com.agnesmaria.retail_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "SKU is required")
    @Schema(description = "Unique SKU for the product", example = "PROD-001")
    private String sku;

    @NotBlank(message = "Product name is required")
    @Schema(description = "Product name", example = "Laptop ASUS ROG")
    private String name;

    @Schema(description = "Product description", example = "High performance gaming laptop")
    private String description;

    @NotNull(message = "Price must not be null")
    @Positive(message = "Price must be greater than 0")
    @Schema(description = "Product price", example = "20000000")
    private BigDecimal price;

    @Min(value = 0, message = "Stock cannot be negative")
    @Schema(description = "Available stock in retail system", example = "10")
    private Integer stock;

    @NotNull(message = "Active status must be specified")
    @Schema(description = "Whether the product is active", example = "true")
    private Boolean active;
}
