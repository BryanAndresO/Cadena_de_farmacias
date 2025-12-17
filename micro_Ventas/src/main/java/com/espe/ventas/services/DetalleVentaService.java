package com.espe.ventas.services;

import com.espe.ventas.models.entities.DetalleVenta;
import java.util.List;
import java.util.Optional;

public interface DetalleVentaService {
    List<DetalleVenta> buscarTodos();
    Optional<DetalleVenta> buscarPorID(Long id);
    DetalleVenta guardar(DetalleVenta detalleVenta);
    void eliminar(Long id);
}

