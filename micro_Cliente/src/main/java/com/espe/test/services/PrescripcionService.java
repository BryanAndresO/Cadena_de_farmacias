package com.espe.test.services;

import com.espe.test.models.entities.Prescripcion;
import java.util.List;
import java.util.Optional;

public interface PrescripcionService {
    List<Prescripcion> buscarTodos();
    Optional<Prescripcion> buscarPorID(Long id);
    Prescripcion guardar(Prescripcion prescripcion);
    void eliminar(Long id);
}
