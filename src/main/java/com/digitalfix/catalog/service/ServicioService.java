package com.digitalfix.catalog.service;

import com.digitalfix.catalog.domain.Servicio;
import com.digitalfix.catalog.dto.ServicioRequest;
import com.digitalfix.catalog.dto.ServicioResponse;
import com.digitalfix.catalog.exception.CatalogAccessDeniedException;
import com.digitalfix.catalog.exception.ConflictException;
import com.digitalfix.catalog.exception.ResourceNotFoundException;
import com.digitalfix.catalog.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioService {

    private final ServicioRepository repository;

    @Transactional(readOnly = true)
    public List<ServicioResponse> findAll(Boolean soloActivos) {
        List<Servicio> list = Boolean.TRUE.equals(soloActivos) ? repository.findByActivoTrue() : repository.findAll();
        return list.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ServicioResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public ServicioResponse create(ServicioRequest req, String email, String role) {
        assertSupervisor(role);
        if (repository.findByNombre(req.nombre()).isPresent()) {
            throw new ConflictException("Servicio ya existe con nombre: " + req.nombre());
        }
        // Sin responsable explícito el item queda asignado a quien lo crea
        String responsable = (req.responsable() == null || req.responsable().isBlank()) ? email : req.responsable();
        Servicio s = Servicio.builder()
                .nombre(req.nombre())
                .descripcion(req.descripcion())
                .tarifa(req.tarifa())
                .categoria(req.categoria())
                .activo(req.activo() != null ? req.activo() : true)
                .responsable(responsable)
                .build();
        return toResponse(repository.save(s));
    }

    @Transactional
    public ServicioResponse update(Long id, ServicioRequest req, String email, String role) {
        Servicio s = getOrThrow(id);
        assertCanManage(s.getResponsable(), email, role);
        repository.findByNombre(req.nombre()).ifPresent(other -> {
            if (!other.getId().equals(id)) throw new ConflictException("Otro servicio ya usa el nombre: " + req.nombre());
        });
        s.setNombre(req.nombre());
        s.setDescripcion(req.descripcion());
        s.setTarifa(req.tarifa());
        s.setCategoria(req.categoria());
        if (req.activo() != null) s.setActivo(req.activo());
        if (req.responsable() != null) s.setResponsable(req.responsable().isBlank() ? null : req.responsable());
        return toResponse(repository.save(s));
    }

    @Transactional
    public void delete(Long id, String email, String role) {
        Servicio s = getOrThrow(id);
        assertCanManage(s.getResponsable(), email, role);
        repository.delete(s);
    }

    private void assertSupervisor(String role) {
        if (!"Supervisor".equalsIgnoreCase(role)) {
            throw new CatalogAccessDeniedException("Solo el Supervisor puede gestionar el catálogo");
        }
    }

    private void assertCanManage(String responsable, String email, String role) {
        assertSupervisor(role);
        if (responsable != null && !responsable.isBlank()
                && (email == null || !responsable.equalsIgnoreCase(email))) {
            throw new CatalogAccessDeniedException("El item está asignado a otro supervisor: " + responsable);
        }
    }

    private Servicio getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado: " + id));
    }

    private ServicioResponse toResponse(Servicio s) {
        return new ServicioResponse(s.getId(), s.getNombre(), s.getDescripcion(), s.getTarifa(), s.getCategoria(), s.getActivo(), s.getResponsable(), s.getCreatedAt(), s.getUpdatedAt());
    }
}
