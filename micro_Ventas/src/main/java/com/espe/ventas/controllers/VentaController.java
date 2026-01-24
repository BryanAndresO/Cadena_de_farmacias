package com.espe.ventas.controllers;

import com.espe.ventas.dtos.VentaDTO;
import com.espe.ventas.exceptions.ResourceNotFoundException;
import com.espe.ventas.services.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ventas")
public class VentaController {
    @Autowired
    private VentaService service;

    @GetMapping("/")
    public ResponseEntity<List<VentaDTO>> listar() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PostMapping("/")
    public ResponseEntity<VentaDTO> crear(@jakarta.validation.Valid @RequestBody VentaDTO ventaDTO) {
        VentaDTO ventaGuardada = service.guardar(ventaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaGuardada);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<VentaDTO> editar(@jakarta.validation.Valid @RequestBody VentaDTO ventaDTO,
            @PathVariable Long id) {
        Optional<VentaDTO> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            throw new ResourceNotFoundException("Venta no encontrada con ID: " + id);
        }
        VentaDTO ventaExistente = o.get();
        // Update fields from DTO
        ventaExistente.setFecha(ventaDTO.getFecha());
        ventaExistente.setTotal(ventaDTO.getTotal());
        ventaExistente.setClienteId(ventaDTO.getClienteId());
        ventaExistente.setSucursalId(ventaDTO.getSucursalId());

        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(ventaExistente));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<VentaDTO> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            throw new ResourceNotFoundException("Venta no encontrada con ID: " + id);
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
