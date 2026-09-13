package com.digitalfix.catalog.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "repuestos", uniqueConstraints = @UniqueConstraint(columnNames = "sku"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Repuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @Column(length = 2000)
    private String descripcion;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String sku;

    @NotNull
    @Positive
    @Column(nullable = false)
    private BigDecimal precio;

    @NotNull
    @Min(0)
    @Builder.Default
    @Column(nullable = false)
    private Integer stock = 0;

    @Min(0)
    @Builder.Default
    @Column(nullable = false)
    private Integer stockMinimo = 5;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (stock == null) stock = 0;
        if (stockMinimo == null) stockMinimo = 5;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean stockBajo() {
        return stock <= stockMinimo;
    }
}
