package com.agnesmaria.retail_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "retail_warehouses", uniqueConstraints = {
        @UniqueConstraint(name = "uk_retail_warehouse_code", columnNames = "code")
})
public class RetailWarehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String code;

    @NotBlank @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String name;

    @Size(max = 255)
    @Column(length = 255)
    private String address;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
