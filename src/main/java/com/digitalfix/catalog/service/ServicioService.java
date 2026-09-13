package com.digitalfix.catalog.service;

import com.digitalfix.catalog.domain.Servicio;
import com.digitalfix.catalog.dto.ServicioRequest;
import com.digitalfix.catalog.dto.ServicioResponse;
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
    public ServicioResponse create(ServicioRequest req) {
        if (repository.findByNombre(req.nombre()).isPresent()) {
            throw new ConflictException("Servicio ya existe con nombre: " + req.nombre());
        }
        Servicio s = Servicio.builder()
                .nombre(req.nombre())
                .descripcion(req.descripcion())
                .tarifa(req.tarifa())
                .categoria(req.categoria())
                .activo(req.activo() != null ? req.activo() : true)
                .build();
        return toResponse(repository.save(s));
    }

    @Transactional
    public ServicioResponse update(Long id, ServicioRequest req) {
        Servicio s = getOrThrow(id);
        repository.findByNombre(req.nombre()).ifPresent(other -> {
            if (!other.getId().equals(id)) throw new ConflictException("Otro servicio ya usa el nombre: " + req.nombre());
        });
        s.setNombre(req.nombre());
        s.setDescripcion(req.descripcion());
        s.setTarifa(req.tarifa());
        s.setCategoria(req.categoria());
        if (req.activo() != null) s.setActivo(req.activo());
        return toResponse(repository.save(s));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Servicio getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado: " + id));
    }

    private ServicioResponse toResponse(Servicio s) {
        return new ServicioResponse(s.getId(), s.getNombre(), s.getDescripcion(), s.getTarifa(), s.getCategoria(), s.getActivo(), s.getCreatedAt(), s.getUpdatedAt());
    }
}
