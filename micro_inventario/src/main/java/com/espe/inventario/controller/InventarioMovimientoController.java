package com.espe.inventario.controller;

import com.espe.inventario.dto.InventarioMovimientoDTO;
import com.espe.inventario.service.InventarioMovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventario/movimientos")
public class InventarioMovimientoController {

    @Autowired
    private InventarioMovimientoService service;

    /**
     * GET /inventario/movimientos - Obtener todos los movimientos del historial
     */
    @GetMapping
    public ResponseEntity<List<InventarioMovimientoDTO>> findAll() {
        List<InventarioMovimientoDTO> movimientos = service.findAll();
        return ResponseEntity.ok(movimientos);
    }

    /**
     * GET /inventario/movimientos/sucursal/{sucursalId} - Obtener movimientos por sucursal
     */
    @GetMapping("/sucursal/{sucursalId}")
    public ResponseEntity<List<InventarioMovimientoDTO>> findBySucursal(@PathVariable Long sucursalId) {
        List<InventarioMovimientoDTO> movimientos = service.findBySucursal(sucursalId);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * GET /inventario/movimientos/producto/{productoId} - Obtener movimientos por producto
     */
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<InventarioMovimientoDTO>> findByProducto(@PathVariable String productoId) {
        List<InventarioMovimientoDTO> movimientos = service.findByProducto(productoId);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * GET /inventario/movimientos/sucursal/{sucursalId}/producto/{productoId} - Obtener movimientos específicos
     */
    @GetMapping("/sucursal/{sucursalId}/producto/{productoId}")
    public ResponseEntity<List<InventarioMovimientoDTO>> findBySucursalAndProducto(
            @PathVariable Long sucursalId,
            @PathVariable String productoId) {
        List<InventarioMovimientoDTO> movimientos = service.findBySucursalAndProducto(sucursalId, productoId);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * GET /inventario/movimientos/inventario/{inventarioId} - Obtener movimientos por inventario
     */
    @GetMapping("/inventario/{inventarioId}")
    public ResponseEntity<List<InventarioMovimientoDTO>> findByInventario(@PathVariable Long inventarioId) {
        List<InventarioMovimientoDTO> movimientos = service.findByInventario(inventarioId);
        return ResponseEntity.ok(movimientos);
    }
}
