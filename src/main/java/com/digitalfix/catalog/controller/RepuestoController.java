package com.digitalfix.catalog.controller;

import com.digitalfix.catalog.dto.RepuestoRequest;
import com.digitalfix.catalog.dto.RepuestoResponse;
import com.digitalfix.catalog.dto.StockAdjustRequest;
import com.digitalfix.catalog.service.RepuestoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/repuestos")
@RequiredArgsConstructor
public class RepuestoController {

    private final RepuestoService service;

    @GetMapping
    public List<RepuestoResponse> list() {
        return service.findAll();
    }

    @GetMapping("/stock-bajo")
    public List<RepuestoResponse> stockBajo() {
        return service.findStockBajo();
    }

    @GetMapping("/{id}")
    public RepuestoResponse get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RepuestoResponse create(@Valid @RequestBody RepuestoRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public RepuestoResponse update(@PathVariable Long id, @Valid @RequestBody RepuestoRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Regla clave: descontar stock al asignar orden
    @PostMapping("/{id}/descontar")
    public RepuestoResponse descontar(@PathVariable Long id, @Valid @RequestBody StockAdjustRequest req) {
        return service.descontarStock(id, req.cantidad());
    }

    @PostMapping("/{id}/reponer")
    public RepuestoResponse reponer(@PathVariable Long id, @Valid @RequestBody StockAdjustRequest req) {
        return service.reponerStock(id, req.cantidad());
    }

    @PostMapping("/{id}/consumir")
    public RepuestoResponse consumirParaOrden(@PathVariable Long id) {
        return service.consumirParaOrden(id);
    }
}
