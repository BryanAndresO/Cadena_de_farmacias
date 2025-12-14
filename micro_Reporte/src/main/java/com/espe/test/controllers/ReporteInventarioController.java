package com.espe.test.controllers;

import com.espe.test.models.entities.ReporteInventario;
import com.espe.test.services.ReporteInventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reporte-inventarios")
public class ReporteInventarioController {
    @Autowired
    private ReporteInventarioService service;

    @GetMapping("/")
    public ResponseEntity<List<ReporteInventario>> listar(){
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PostMapping("/")
    public ResponseEntity<?> crear(@RequestBody ReporteInventario reporteInventario){
        ReporteInventario reporteInventarioDB=service.guardar(reporteInventario);
        return ResponseEntity.status(HttpStatus.CREATED).body(reporteInventarioDB);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@RequestBody ReporteInventario reporteInventario, @PathVariable Long id){
        Optional<ReporteInventario> o = service.buscarPorID(id);
        if(o.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        ReporteInventario reporteInventarioDB = o.get();
        reporteInventarioDB.setFecha(reporteInventario.getFecha());
        reporteInventarioDB.setSucursalId(reporteInventario.getSucursalId());
        reporteInventarioDB.setMedicamentoId(reporteInventario.getMedicamentoId());
        reporteInventarioDB.setStock(reporteInventario.getStock());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(reporteInventarioDB));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        Optional<ReporteInventario> o = service.buscarPorID(id);
        if(o.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
