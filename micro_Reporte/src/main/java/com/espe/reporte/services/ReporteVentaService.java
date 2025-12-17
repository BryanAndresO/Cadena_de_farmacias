package com.espe.reporte.services;

import com.espe.reporte.models.entities.ReporteVenta;
import com.espe.reporte.dtos.ReporteVentaDTO;
import java.util.List;
import java.util.Optional;

public interface ReporteVentaService {
    List<ReporteVentaDTO> buscarTodos();

    Optional<ReporteVentaDTO> buscarPorID(Long id);

    ReporteVentaDTO guardar(ReporteVentaDTO reporteVentaDTO);

    void eliminar(Long id);
}
