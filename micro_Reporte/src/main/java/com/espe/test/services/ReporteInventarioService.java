package com.espe.test.services;

import com.espe.test.models.entities.ReporteInventario;
import java.util.List;
import java.util.Optional;

public interface ReporteInventarioService {
    List<ReporteInventario> buscarTodos();
    Optional<ReporteInventario> buscarPorID(Long id);
    ReporteInventario guardar(ReporteInventario reporteInventario);
    void eliminar(Long id);
}
