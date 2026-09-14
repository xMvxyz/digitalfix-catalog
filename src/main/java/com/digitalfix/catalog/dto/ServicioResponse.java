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
        String responsable,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
