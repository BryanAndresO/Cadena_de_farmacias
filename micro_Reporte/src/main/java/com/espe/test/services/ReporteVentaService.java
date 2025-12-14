package com.espe.test.services;

import com.espe.test.models.entities.ReporteVenta;
import java.util.List;
import java.util.Optional;

public interface ReporteVentaService {
    List<ReporteVenta> buscarTodos();
    Optional<ReporteVenta> buscarPorID(Long id);
    ReporteVenta guardar(ReporteVenta reporteVenta);
    void eliminar(Long id);
}
