package com.espe.reporte.controllers;

import com.espe.reporte.dtos.ReporteInventarioDTO;
import com.espe.reporte.exceptions.ResourceNotFoundException;
import com.espe.reporte.services.ReporteInventarioService;
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
    public ResponseEntity<List<ReporteInventarioDTO>> listar() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PostMapping("/")
    public ResponseEntity<ReporteInventarioDTO> crear(@RequestBody ReporteInventarioDTO reporteInventarioDTO) {
        ReporteInventarioDTO reporteInventarioDB = service.guardar(reporteInventarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(reporteInventarioDB);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReporteInventarioDTO> editar(@RequestBody ReporteInventarioDTO reporteInventarioDTO,
            @PathVariable Long id) {
        Optional<ReporteInventarioDTO> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            throw new ResourceNotFoundException("Reporte de inventario no encontrado con ID: " + id);
        }
        ReporteInventarioDTO reporteExistente = o.get();
        reporteExistente.setFecha(reporteInventarioDTO.getFecha());
        reporteExistente.setSucursalId(reporteInventarioDTO.getSucursalId());
        reporteExistente.setMedicamentoId(reporteInventarioDTO.getMedicamentoId());
        reporteExistente.setStock(reporteInventarioDTO.getStock());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(reporteExistente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<ReporteInventarioDTO> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            throw new ResourceNotFoundException("Reporte de inventario no encontrado con ID: " + id);
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
