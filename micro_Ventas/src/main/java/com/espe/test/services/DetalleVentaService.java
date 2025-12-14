package com.espe.test.services;

import com.espe.test.models.entities.DetalleVenta;
import java.util.List;
import java.util.Optional;

public interface DetalleVentaService {
    List<DetalleVenta> buscarTodos();
    Optional<DetalleVenta> buscarPorID(Long id);
    DetalleVenta guardar(DetalleVenta detalleVenta);
    void eliminar(Long id);
}
