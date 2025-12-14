package com.espe.test.controllers;

import com.espe.test.models.entities.Medicamento;
import com.espe.test.services.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;

@RestController
public class MedicamentoController {
    @Autowired
    private MedicamentoService service;

    @GetMapping("/")
    public ResponseEntity<List<Medicamento>> listar(){
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PostMapping("/")
    public ResponseEntity<?> crear(@RequestBody Medicamento medicamento){
        Medicamento medicamentoDB=service.guardar(medicamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(medicamentoDB);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@RequestBody Medicamento medicamento, @PathVariable Long id){
        Optional<Medicamento> o = service.buscarPorID(id);
        if(o.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Medicamento medicamentoDB = o.get();
        medicamentoDB.setNombre(medicamento.getNombre());
        medicamentoDB.setDescripcion(medicamento.getDescripcion());
        medicamentoDB.setLaboratorio(medicamento.getLaboratorio());
        medicamentoDB.setPrecio(medicamento.getPrecio());
        medicamentoDB.setStock(medicamento.getStock());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(medicamentoDB));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        Optional<Medicamento> o = service.buscarPorID(id);
        if(o.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
