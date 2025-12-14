package com.espe.test.services;

import com.espe.test.models.entities.Sucursal;
import java.util.List;
import java.util.Optional;

public interface SucursalService {
    List<Sucursal> buscarTodos();
    Optional<Sucursal> buscarPorID(Long id);
    Sucursal guardar(Sucursal sucursal);
    void eliminar(Long id);
}
