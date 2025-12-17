package com.espe.ventas.services;

import com.espe.ventas.models.entities.Venta;
import com.espe.ventas.dtos.VentaDTO;
import java.util.List;
import java.util.Optional;

public interface VentaService {
    List<VentaDTO> buscarTodos();

    Optional<VentaDTO> buscarPorID(Long id);

    VentaDTO guardar(VentaDTO ventaDTO);

    void eliminar(Long id);
}
