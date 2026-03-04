package com.agnesmaria.retail_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO untuk permintaan restock dari retail ke warehouse pusat.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestockRequest {

    private Long fromWarehouseId;  //  Gudang pusat (sumber)
    private Long toWarehouseId;    //  Gudang retail (tujuan)
    private String productSku;     //  SKU produk
    private Integer quantity;      //  Jumlah barang
}
