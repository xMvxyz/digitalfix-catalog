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
    public RepuestoResponse create(@Valid @RequestBody RepuestoRequest req,
                                   @RequestHeader(value = "X-User-Email", required = false) String email,
                                   @RequestHeader(value = "X-User-Role", required = false) String role) {
        return service.create(req, email, role);
    }

    @PutMapping("/{id}")
    public RepuestoResponse update(@PathVariable Long id, @Valid @RequestBody RepuestoRequest req,
                                   @RequestHeader(value = "X-User-Email", required = false) String email,
                                   @RequestHeader(value = "X-User-Role", required = false) String role) {
        return service.update(id, req, email, role);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader(value = "X-User-Email", required = false) String email,
                                       @RequestHeader(value = "X-User-Role", required = false) String role) {
        service.delete(id, email, role);
        return ResponseEntity.noContent().build();
    }

    // Regla clave: descontar stock al asignar orden
    @PostMapping("/{id}/descontar")
    public RepuestoResponse descontar(@PathVariable Long id, @Valid @RequestBody StockAdjustRequest req,
                                      @RequestHeader(value = "X-User-Email", required = false) String email,
                                      @RequestHeader(value = "X-User-Role", required = false) String role) {
        return service.descontarStock(id, req.cantidad(), email, role);
    }

    @PostMapping("/{id}/reponer")
    public RepuestoResponse reponer(@PathVariable Long id, @Valid @RequestBody StockAdjustRequest req,
                                    @RequestHeader(value = "X-User-Email", required = false) String email,
                                    @RequestHeader(value = "X-User-Role", required = false) String role) {
        return service.reponerStock(id, req.cantidad(), email, role);
    }

    @PostMapping("/{id}/consumir")
    public RepuestoResponse consumirParaOrden(@PathVariable Long id) {
        return service.consumirParaOrden(id);
    }
}
