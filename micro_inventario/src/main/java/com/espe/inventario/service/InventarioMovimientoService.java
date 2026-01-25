package com.espe.inventario.service;

import com.espe.inventario.dto.InventarioMovimientoDTO;
import java.util.List;

public interface InventarioMovimientoService {
    
    // Obtener todos los movimientos
    List<InventarioMovimientoDTO> findAll();
    
    // Obtener movimientos por sucursal
    List<InventarioMovimientoDTO> findBySucursal(Long sucursalId);
    
    // Obtener movimientos por producto
    List<InventarioMovimientoDTO> findByProducto(String productoId);
    
    // Obtener movimientos por sucursal y producto
    List<InventarioMovimientoDTO> findBySucursalAndProducto(Long sucursalId, String productoId);
    
    // Obtener movimientos por inventario específico
    List<InventarioMovimientoDTO> findByInventario(Long inventarioId);
}
