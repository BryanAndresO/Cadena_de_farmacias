package com.espe.inventario.controller;

import com.espe.inventario.dto.InventarioCreateDTO;
import com.espe.inventario.dto.InventarioDTO;
import com.espe.inventario.dto.InventarioUpdateDTO;
import com.espe.inventario.dto.StockAdjustmentDTO;
import com.espe.inventario.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventario")
public class InventarioController {

    @Autowired
    private InventarioService service;

    /**
     * GET /api/inventario - Get all inventory records
     */
    @GetMapping
    public ResponseEntity<List<InventarioDTO>> findAll() {
        List<InventarioDTO> inventarios = service.findAll();
        return ResponseEntity.ok(inventarios);
    }

    /**
     * GET /api/inventario/{id} - Get inventory by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<InventarioDTO> findById(@PathVariable Long id) {
        InventarioDTO inventario = service.findById(id);
        return ResponseEntity.ok(inventario);
    }

    /**
     * GET /api/inventario/sucursal/{sucursalId} - Get inventory by branch
     */
    @GetMapping("/sucursal/{sucursalId}")
    public ResponseEntity<List<InventarioDTO>> findBySucursal(@PathVariable Long sucursalId) {
        List<InventarioDTO> inventarios = service.findBySucursal(sucursalId);
        return ResponseEntity.ok(inventarios);
    }

    /**
     * GET /api/inventario/producto/{productoId} - Get inventory by product
     */
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<InventarioDTO>> findByProducto(@PathVariable String productoId) {
        List<InventarioDTO> inventarios = service.findByProducto(productoId);
        return ResponseEntity.ok(inventarios);
    }

    /**
     * GET /api/inventario/sucursal/{sucursalId}/producto/{productoId} - Get
     * specific inventory
     */
    @GetMapping("/sucursal/{sucursalId}/producto/{productoId}")
    public ResponseEntity<InventarioDTO> findBySucursalAndProducto(
            @PathVariable Long sucursalId,
            @PathVariable String productoId) {
        InventarioDTO inventario = service.findBySucursalAndProducto(sucursalId, productoId);
        return ResponseEntity.ok(inventario);
    }

    /**
     * GET /api/inventario/low-stock - Get all low stock items
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<InventarioDTO>> findLowStockItems() {
        List<InventarioDTO> inventarios = service.findLowStockItems();
        return ResponseEntity.ok(inventarios);
    }

    /**
     * GET /api/inventario/sucursal/{sucursalId}/low-stock - Get low stock items by
     * branch
     */
    @GetMapping("/sucursal/{sucursalId}/low-stock")
    public ResponseEntity<List<InventarioDTO>> findLowStockItemsBySucursal(@PathVariable Long sucursalId) {
        List<InventarioDTO> inventarios = service.findLowStockItemsBySucursal(sucursalId);
        return ResponseEntity.ok(inventarios);
    }

    /**
     * POST /api/inventario - Create new inventory record
     */
    @PostMapping({ "", "/" })
    public ResponseEntity<InventarioDTO> create(@Valid @RequestBody InventarioCreateDTO createDTO) {
        InventarioDTO created = service.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/inventario/{id} - Update inventory record
     */
    @PutMapping("/{id}")
    public ResponseEntity<InventarioDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody InventarioUpdateDTO updateDTO) {
        InventarioDTO updated = service.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * PATCH /api/inventario/{id}/adjust - Adjust stock
     */
    @PatchMapping("/{id}/adjust")
    public ResponseEntity<InventarioDTO> adjustStock(
            @PathVariable Long id,
            @Valid @RequestBody StockAdjustmentDTO adjustmentDTO) {
        InventarioDTO adjusted = service.adjustStock(id, adjustmentDTO.getAdjustment());
        return ResponseEntity.ok(adjusted);
    }

    /**
     * DELETE /api/inventario/{id} - Delete inventory record
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
