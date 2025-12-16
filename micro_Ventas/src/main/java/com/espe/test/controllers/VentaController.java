package com.espe.test.controllers;

import com.espe.test.models.entities.Venta;
import com.espe.test.services.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ventas")
public class VentaController {
    @Autowired
    private VentaService service;

    @GetMapping("/")
    public ResponseEntity<List<Venta>> listar() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PostMapping("/")
    public ResponseEntity<?> crear(@jakarta.validation.Valid @RequestBody Venta venta) {
        Venta ventaDB = service.guardar(venta);
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaDB);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@jakarta.validation.Valid @RequestBody Venta venta, @PathVariable Long id) {
        Optional<Venta> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Venta ventaDB = o.get();
        ventaDB.setFecha(venta.getFecha());
        ventaDB.setTotal(venta.getTotal());
        ventaDB.setClienteId(venta.getClienteId());
        ventaDB.setSucursalId(venta.getSucursalId());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(ventaDB));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<Venta> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
