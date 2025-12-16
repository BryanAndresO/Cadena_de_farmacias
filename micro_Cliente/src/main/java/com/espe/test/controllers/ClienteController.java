package com.espe.test.controllers;

import com.espe.test.models.entities.Cliente;
import com.espe.test.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    @Autowired
    private ClienteService service;

    @GetMapping("/")
    public ResponseEntity<List<Cliente>> listar() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PostMapping("/")
    public ResponseEntity<?> crear(@jakarta.validation.Valid @RequestBody Cliente cliente) {
        Cliente clienteDB = service.guardar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteDB);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@jakarta.validation.Valid @RequestBody Cliente cliente, @PathVariable Long id) {
        Optional<Cliente> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Cliente clienteDB = o.get();
        clienteDB.setNombre(cliente.getNombre());
        clienteDB.setCedula(cliente.getCedula());
        clienteDB.setDireccion(cliente.getDireccion());
        clienteDB.setTelefono(cliente.getTelefono());
        clienteDB.setEmail(cliente.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(clienteDB));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<Cliente> o = service.buscarPorID(id);
        if (o.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
