package com.digitalfix.catalog.controller;

import com.digitalfix.catalog.dto.ServicioRequest;
import com.digitalfix.catalog.dto.ServicioResponse;
import com.digitalfix.catalog.service.ServicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/servicios")
@RequiredArgsConstructor
public class ServicioController {

    private final ServicioService service;

    @GetMapping
    public List<ServicioResponse> list(@RequestParam(required = false) Boolean soloActivos) {
        return service.findAll(soloActivos);
    }

    @GetMapping("/{id}")
    public ServicioResponse get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServicioResponse create(@Valid @RequestBody ServicioRequest req,
                                   @RequestHeader(value = "X-User-Email", required = false) String email,
                                   @RequestHeader(value = "X-User-Role", required = false) String role) {
        return service.create(req, email, role);
    }

    @PutMapping("/{id}")
    public ServicioResponse update(@PathVariable Long id, @Valid @RequestBody ServicioRequest req,
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
}
