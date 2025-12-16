package com.espe.test.controllers;

import com.espe.test.models.entities.DetalleVenta;
import com.espe.test.services.DetalleVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/detalle-ventas")
public class DetalleVentaController {
    @Autowired
    private DetalleVentaService service;

    @GetMapping("/")
    public ResponseEntity<List<DetalleVenta>> listar() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PostMapping("/")
    public ResponseEntity<?> crear(@jakarta.validation.Valid @RequestBody DetalleVenta detalleVenta) {
        DetalleVenta detalleVentaDB = service.guardar(detalleVenta);
        return ResponseEntity.status(HttpStatus.CREATED).body(detalleVentaDB);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@jakarta.validation.Valid @RequestBody DetalleVenta detalleVenta,
            @PathVariable Long id) {
        Optional<DetalleVenta> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        DetalleVenta detalleVentaDB = o.get();
        detalleVentaDB.setVentaId(detalleVenta.getVentaId());
        detalleVentaDB.setMedicamentoId(detalleVenta.getMedicamentoId());
        detalleVentaDB.setCantidad(detalleVenta.getCantidad());
        detalleVentaDB.setPrecioUnitario(detalleVenta.getPrecioUnitario());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(detalleVentaDB));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<DetalleVenta> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
