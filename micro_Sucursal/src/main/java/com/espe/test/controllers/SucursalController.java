package com.espe.test.controllers;

import com.espe.test.models.entities.Sucursal;
import com.espe.test.services.SucursalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sucursales")
public class SucursalController {
    @Autowired
    private SucursalService service;

    @GetMapping("/")
    public ResponseEntity<List<Sucursal>> listar(){
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PostMapping("/")
    public ResponseEntity<?> crear(@RequestBody Sucursal sucursal){
        Sucursal sucursalDB=service.guardar(sucursal);
        return ResponseEntity.status(HttpStatus.CREATED).body(sucursalDB);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@RequestBody Sucursal sucursal, @PathVariable Long id){
        Optional<Sucursal> o = service.buscarPorID(id);
        if(o.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Sucursal sucursalDB = o.get();
        sucursalDB.setNombre(sucursal.getNombre());
        sucursalDB.setCiudad(sucursal.getCiudad());
        sucursalDB.setDireccion(sucursal.getDireccion());
        sucursalDB.setTelefono(sucursal.getTelefono());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(sucursalDB));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        Optional<Sucursal> o = service.buscarPorID(id);
        if(o.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
