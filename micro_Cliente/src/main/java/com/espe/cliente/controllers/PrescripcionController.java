package com.espe.cliente.controllers;

import com.espe.cliente.models.entities.Prescripcion;
import com.espe.cliente.services.PrescripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/prescripciones")
public class PrescripcionController {
    @Autowired
    private PrescripcionService service;

    @GetMapping("/")
    public ResponseEntity<List<Prescripcion>> listar() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PostMapping("/")
    public ResponseEntity<?> crear(@jakarta.validation.Valid @RequestBody Prescripcion prescripcion) {
        Prescripcion prescripcionDB = service.guardar(prescripcion);
        return ResponseEntity.status(HttpStatus.CREATED).body(prescripcionDB);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@jakarta.validation.Valid @RequestBody Prescripcion prescripcion,
            @PathVariable Long id) {
        Optional<Prescripcion> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Prescripcion prescripcionDB = o.get();
        prescripcionDB.setDescripcion(prescripcion.getDescripcion());
        prescripcionDB.setFecha(prescripcion.getFecha());
        prescripcionDB.setClienteId(prescripcion.getClienteId());
        prescripcionDB.setMedicamentoId(prescripcion.getMedicamentoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(prescripcionDB));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<Prescripcion> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

