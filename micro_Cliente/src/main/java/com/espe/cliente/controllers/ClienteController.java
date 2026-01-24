package com.espe.cliente.controllers;

import com.espe.cliente.dtos.ClienteDTO;
import com.espe.cliente.exceptions.ResourceNotFoundException;
import com.espe.cliente.services.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @GetMapping("/")
    public ResponseEntity<List<ClienteDTO>> listar() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PostMapping("/")
    public ResponseEntity<ClienteDTO> crear(@Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO clienteDB = service.guardar(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteDB);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> editar(@Valid @RequestBody ClienteDTO clienteDTO, @PathVariable Long id) {
        Optional<ClienteDTO> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            throw new ResourceNotFoundException("No se encontro el cliente con el id: " + id);
        }
        clienteDTO.setId(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(clienteDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<ClienteDTO> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            throw new ResourceNotFoundException("No se encontro el cliente con el id: " + id);
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
