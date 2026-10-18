package com.digitalfix.catalog.service;

import com.digitalfix.catalog.domain.Repuesto;
import com.digitalfix.catalog.dto.RepuestoRequest;
import com.digitalfix.catalog.dto.RepuestoResponse;
import com.digitalfix.catalog.exception.CatalogAccessDeniedException;
import com.digitalfix.catalog.exception.ConflictException;
import com.digitalfix.catalog.exception.InsufficientStockException;
import com.digitalfix.catalog.exception.ResourceNotFoundException;
import com.digitalfix.catalog.repository.RepuestoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepuestoService {

    private final RepuestoRepository repository;

    @Transactional(readOnly = true)
    public List<RepuestoResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public RepuestoResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<RepuestoResponse> findStockBajo() {
        return repository.findAll().stream()
                .filter(Repuesto::stockBajo)
                .map(this::toResponse).toList();
    }

    @Transactional
    public RepuestoResponse create(RepuestoRequest req, String email, String role) {
        assertSupervisor(role);
        if (repository.findBySku(req.sku()).isPresent()) {
            throw new ConflictException("Repuesto ya existe con SKU: " + req.sku());
        }
        // Sin responsable explícito el item queda asignado a quien lo crea
        String responsable = (req.responsable() == null || req.responsable().isBlank()) ? email : req.responsable();
        Repuesto r = Repuesto.builder()
                .nombre(req.nombre())
                .descripcion(req.descripcion())
                .sku(req.sku())
                .precio(req.precio())
                .stock(req.stock())
                .stockMinimo(req.stockMinimo() != null ? req.stockMinimo() : 5)
                .responsable(responsable)
                .build();
        return toResponse(repository.save(r));
    }

    @Transactional
    public RepuestoResponse update(Long id, RepuestoRequest req, String email, String role) {
        Repuesto r = getOrThrow(id);
        assertCanManage(r.getResponsable(), email, role);
        repository.findBySku(req.sku()).ifPresent(other -> {
            if (!other.getId().equals(id)) throw new ConflictException("Otro repuesto ya usa SKU: " + req.sku());
        });
        r.setNombre(req.nombre());
        r.setDescripcion(req.descripcion());
        r.setSku(req.sku());
        r.setPrecio(req.precio());
        r.setStock(req.stock());
        if (req.stockMinimo() != null) r.setStockMinimo(req.stockMinimo());
        if (req.responsable() != null) r.setResponsable(req.responsable().isBlank() ? null : req.responsable());
        return toResponse(repository.save(r));
    }

    @Transactional
    public RepuestoResponse descontarStock(Long id, int cantidad, String email, String role) {
        Repuesto r = getOrThrow(id);
        assertCanManage(r.getResponsable(), email, role);
        return descontar(r, cantidad);
    }

    @Transactional
    public RepuestoResponse reponerStock(Long id, int cantidad, String email, String role) {
        Repuesto r = getOrThrow(id);
        assertCanManage(r.getResponsable(), email, role);
        r.setStock(r.getStock() + cantidad);
        return toResponse(repository.save(r));
    }

    @Transactional
    public RepuestoResponse consumirParaOrden(Long id) {
        // Regla clave: stock disminuye en 1 al asignar orden.
        // Callback interno del BFF (sin alcance de asignación): no se restringe por responsable.
        return descontar(getOrThrow(id), 1);
    }

    private RepuestoResponse descontar(Repuesto r, int cantidad) {
        if (r.getStock() < cantidad) {
            throw new InsufficientStockException("Stock insuficiente para repuesto " + r.getSku() + ": disponible " + r.getStock() + ", requerido " + cantidad);
        }
        r.setStock(r.getStock() - cantidad);
        return toResponse(repository.save(r));
    }

    @Transactional
    public void delete(Long id, String email, String role) {
        Repuesto r = getOrThrow(id);
        assertCanManage(r.getResponsable(), email, role);
        repository.delete(r);
    }

    private void assertSupervisor(String role) {
        if (!"Supervisor".equalsIgnoreCase(role) && !"Admin".equalsIgnoreCase(role)) {
            throw new CatalogAccessDeniedException("Solo Supervisor o Admin pueden gestionar el catálogo");
        }
    }

    private void assertCanManage(String responsable, String email, String role) {
        assertSupervisor(role);
        if (responsable != null && !responsable.isBlank()
                && (email == null || !responsable.equalsIgnoreCase(email))) {
            throw new CatalogAccessDeniedException("El item está asignado a otro supervisor: " + responsable);
        }
    }

    private Repuesto getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Repuesto no encontrado: " + id));
    }

    private RepuestoResponse toResponse(Repuesto r) {
        return new RepuestoResponse(r.getId(), r.getNombre(), r.getDescripcion(), r.getSku(), r.getPrecio(), r.getStock(), r.getStockMinimo(), r.stockBajo(), r.getResponsable(), r.getCreatedAt(), r.getUpdatedAt());
    }
}
