package com.digitalfix.catalog.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.digitalfix.catalog.domain.Repuesto;
import com.digitalfix.catalog.domain.Servicio;
import com.digitalfix.catalog.repository.RepuestoRepository;
import com.digitalfix.catalog.repository.ServicioRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(ServicioRepository servicioRepo, RepuestoRepository repuestoRepo) {
        return args -> {
            if (servicioRepo.count() == 0) {
                servicioRepo.save(Servicio.builder()
                    .nombre("Reparacion Pantalla")
                    .tarifa(new BigDecimal("45000"))
                    .activo(true)
                    .build());
                servicioRepo.save(Servicio.builder()
                    .nombre("Cambio de Bateria")
                    .tarifa(new BigDecimal("25000"))
                    .activo(true)
                    .build());
            }
            if (repuestoRepo.count() == 0) {
                repuestoRepo.save(Repuesto.builder()
                    .nombre("Pantalla Generica")
                    .sku("PANT-GEN-001")
                    .precio(new BigDecimal("20000"))
                    .stock(10)
                    .stockMinimo(5)
                    .build());
                repuestoRepo.save(Repuesto.builder()
                    .nombre("Bateria Original")
                    .sku("BATT-ORIG-001")
                    .precio(new BigDecimal("15000"))
                    .stock(5)
                    .stockMinimo(5)
                    .build());
            }
            System.out.println("Base de datos de Catálogo inicializada.");
        };
    }
}