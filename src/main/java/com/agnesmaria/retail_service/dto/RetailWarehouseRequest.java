package com.agnesmaria.retail_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RetailWarehouseRequest {
    @NotBlank @Size(max = 50)
    private String code;

    @NotBlank @Size(max = 150)
    private String name;

    @Size(max = 255)
    private String address;

    private Boolean active = true;
}
