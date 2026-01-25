package com.espe.inventario.repository;

import com.espe.inventario.model.InventarioMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarioMovimientoRepository extends JpaRepository<InventarioMovimiento, Long> {

    // Buscar movimientos por sucursal
    List<InventarioMovimiento> findBySucursalIdOrderByFechaMovimientoDesc(Long sucursalId);

    // Buscar movimientos por producto
    List<InventarioMovimiento> findByProductoIdOrderByFechaMovimientoDesc(String productoId);

    // Buscar movimientos por sucursal y producto
    List<InventarioMovimiento> findBySucursalIdAndProductoIdOrderByFechaMovimientoDesc(Long sucursalId, String productoId);

    // Buscar movimientos por inventario específico
    List<InventarioMovimiento> findByInventarioIdOrderByFechaMovimientoDesc(Long inventarioId);

    // Obtener historial completo ordenado por fecha
    List<InventarioMovimiento> findAllByOrderByFechaMovimientoDesc();
}
