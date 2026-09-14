package com.digitalfix.catalog.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RepuestoResponse(
        Long id,
        String nombre,
        String descripcion,
        String sku,
        BigDecimal precio,
        Integer stock,
        Integer stockMinimo,
        boolean stockBajo,
        String responsable,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
