package com.digitalfix.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ServicioRequest(
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 2000) String descripcion,
        @NotNull @Positive BigDecimal tarifa,
        @Size(max = 100) String categoria,
        Boolean activo,
        @Size(max = 320) String responsable
) {}
