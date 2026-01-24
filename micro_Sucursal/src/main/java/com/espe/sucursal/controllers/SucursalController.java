package com.espe.sucursal.controllers;

import com.espe.sucursal.dtos.SucursalDTO;
import com.espe.sucursal.exceptions.ResourceNotFoundException;
import com.espe.sucursal.services.SucursalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sucursales")
public class SucursalController {

    @Autowired
    private SucursalService service;

    @GetMapping("/")
    public ResponseEntity<List<SucursalDTO>> listar() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<SucursalDTO> crear(@Valid @RequestBody SucursalDTO sucursalDTO) {
        SucursalDTO sucursalDB = service.guardar(sucursalDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(sucursalDB);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SucursalDTO> editar(@Valid @RequestBody SucursalDTO sucursalDTO, @PathVariable Long id) {
        Optional<SucursalDTO> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            throw new ResourceNotFoundException("No se encontro la sucursal con el id: " + id);
        }
        sucursalDTO.setId(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(sucursalDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<SucursalDTO> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            throw new ResourceNotFoundException("No se encontro la sucursal con el id: " + id);
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
