package com.espe.reporte.controllers;

import com.espe.reporte.dtos.ReporteVentaDTO;
import com.espe.reporte.exceptions.ResourceNotFoundException;
import com.espe.reporte.services.ReporteVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reporte-ventas")
public class ReporteVentaController {
    @Autowired
    private ReporteVentaService service;

    @GetMapping("/")
    public ResponseEntity<List<ReporteVentaDTO>> listar() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PostMapping("/")
    public ResponseEntity<ReporteVentaDTO> crear(@RequestBody ReporteVentaDTO reporteVentaDTO) {
        ReporteVentaDTO reporteVentaDB = service.guardar(reporteVentaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(reporteVentaDB);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReporteVentaDTO> editar(@RequestBody ReporteVentaDTO reporteVentaDTO, @PathVariable Long id) {
        Optional<ReporteVentaDTO> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            throw new ResourceNotFoundException("Reporte de venta no encontrado con ID: " + id);
        }
        ReporteVentaDTO reporteExistente = o.get();
        reporteExistente.setFecha(reporteVentaDTO.getFecha());
        reporteExistente.setTotalVentas(reporteVentaDTO.getTotalVentas());
        reporteExistente.setSucursalId(reporteVentaDTO.getSucursalId());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(reporteExistente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<ReporteVentaDTO> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            throw new ResourceNotFoundException("Reporte de venta no encontrado con ID: " + id);
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
