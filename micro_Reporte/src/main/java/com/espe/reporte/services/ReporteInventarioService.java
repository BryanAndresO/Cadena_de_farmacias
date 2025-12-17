package com.espe.reporte.services;

import com.espe.reporte.models.entities.ReporteInventario;
import com.espe.reporte.dtos.ReporteInventarioDTO;
import java.util.List;
import java.util.Optional;

public interface ReporteInventarioService {
    List<ReporteInventarioDTO> buscarTodos();

    Optional<ReporteInventarioDTO> buscarPorID(Long id);

    ReporteInventarioDTO guardar(ReporteInventarioDTO reporteInventarioDTO);

    void eliminar(Long id);
}
