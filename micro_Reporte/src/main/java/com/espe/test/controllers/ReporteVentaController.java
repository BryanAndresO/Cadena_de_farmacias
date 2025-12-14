package com.espe.test.controllers;

import com.espe.test.models.entities.ReporteVenta;
import com.espe.test.services.ReporteVentaService;
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
    public ResponseEntity<List<ReporteVenta>> listar(){
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PostMapping("/")
    public ResponseEntity<?> crear(@RequestBody ReporteVenta reporteVenta){
        ReporteVenta reporteVentaDB=service.guardar(reporteVenta);
        return ResponseEntity.status(HttpStatus.CREATED).body(reporteVentaDB);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@RequestBody ReporteVenta reporteVenta, @PathVariable Long id){
        Optional<ReporteVenta> o = service.buscarPorID(id);
        if(o.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        ReporteVenta reporteVentaDB = o.get();
        reporteVentaDB.setFecha(reporteVenta.getFecha());
        reporteVentaDB.setTotalVentas(reporteVenta.getTotalVentas());
        reporteVentaDB.setSucursalId(reporteVenta.getSucursalId());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(reporteVentaDB));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        Optional<ReporteVenta> o = service.buscarPorID(id);
        if(o.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
