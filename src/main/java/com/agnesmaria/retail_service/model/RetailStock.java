package com.agnesmaria.retail_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "retail_stocks", uniqueConstraints = {
        @UniqueConstraint(name = "uk_retail_stock_wh_product", columnNames = {"warehouse_id", "product_id"})
})
public class RetailStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // warehouse toko
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_retail_stock_warehouse"))
    private RetailWarehouse warehouse;

    // product dari retail-service
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_retail_stock_product"))
    private Product product;

    @Min(0)
    @Column(nullable = false)
    private Integer quantity;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
