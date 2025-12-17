package com.espe.sucursal.services;

import com.espe.sucursal.dtos.SucursalDTO;
import com.espe.sucursal.models.entities.Sucursal;
import java.util.List;
import java.util.Optional;

public interface SucursalService {
    List<SucursalDTO> buscarTodos();

    Optional<SucursalDTO> buscarPorID(Long id);

    SucursalDTO guardar(SucursalDTO sucursalDTO);

    void eliminar(Long id);
}
