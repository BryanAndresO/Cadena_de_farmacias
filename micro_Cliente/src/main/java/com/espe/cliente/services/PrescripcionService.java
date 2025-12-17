package com.espe.cliente.services;

import com.espe.cliente.models.entities.Prescripcion;
import java.util.List;
import java.util.Optional;

public interface PrescripcionService {
    List<Prescripcion> buscarTodos();
    Optional<Prescripcion> buscarPorID(Long id);
    Prescripcion guardar(Prescripcion prescripcion);
    void eliminar(Long id);
}

