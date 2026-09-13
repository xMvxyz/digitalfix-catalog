package com.digitalfix.catalog.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServicioResponse(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal tarifa,
        String categoria,
        Boolean activo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
