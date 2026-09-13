package com.digitalfix.catalog.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record RepuestoRequest(
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 2000) String descripcion,
        @NotBlank @Size(max = 50) String sku,
        @NotNull @Positive BigDecimal precio,
        @NotNull @Min(0) Integer stock,
        @Min(0) Integer stockMinimo
) {}
