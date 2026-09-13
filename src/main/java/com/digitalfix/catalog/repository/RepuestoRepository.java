package com.digitalfix.catalog.repository;

import com.digitalfix.catalog.domain.Repuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RepuestoRepository extends JpaRepository<Repuesto, Long> {
    Optional<Repuesto> findBySku(String sku);
    List<Repuesto> findByStockLessThanEqual(int stock);
    List<Repuesto> findByStockLessThanEqualAndStockMinimo(int stock, int stockMinimo);
}
