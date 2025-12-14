package com.espe.test.services;

import com.espe.test.models.entities.Venta;
import java.util.List;
import java.util.Optional;

public interface VentaService {
    List<Venta> buscarTodos();
    Optional<Venta> buscarPorID(Long id);
    Venta guardar(Venta venta);
    void eliminar(Long id);
}
