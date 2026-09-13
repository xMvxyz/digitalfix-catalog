package com.digitalfix.catalog.repository;

import com.digitalfix.catalog.domain.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    Optional<Servicio> findByNombre(String nombre);
    List<Servicio> findByActivoTrue();
    List<Servicio> findByCategoria(String categoria);
}
