package com.espe.catalogo.controllers;

import com.espe.catalogo.dtos.MedicamentoDTO;
import com.espe.catalogo.exceptions.ResourceNotFoundException;
import com.espe.catalogo.services.MedicamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/medicamentos")
public class MedicamentoController {

    @Autowired
    private MedicamentoService service;

    @GetMapping({ "", "/" })
    public ResponseEntity<List<MedicamentoDTO>> listar() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoDTO> buscar(@PathVariable Long id) {
        return service.buscarPorID(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el medicamento con el id: " + id));
    }

    @PostMapping
    public ResponseEntity<MedicamentoDTO> crear(@Valid @RequestBody MedicamentoDTO medicamentoDTO) {
        MedicamentoDTO medicamentoDB = service.guardar(medicamentoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(medicamentoDB);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MedicamentoDTO> editar(@Valid @RequestBody MedicamentoDTO medicamentoDTO,
            @PathVariable Long id) {
        Optional<MedicamentoDTO> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            throw new ResourceNotFoundException("No se encontro el medicamento con el id: " + id);
        }
        medicamentoDTO.setId(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(medicamentoDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<MedicamentoDTO> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            throw new ResourceNotFoundException("No se encontro el medicamento con el id: " + id);
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
