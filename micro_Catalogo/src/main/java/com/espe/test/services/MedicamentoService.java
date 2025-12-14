package com.espe.test.services;

import com.espe.test.models.entities.Medicamento;

import java.util.List;
import java.util.Optional;

public interface MedicamentoService {


    List<Medicamento> buscarTodos();

    Optional<Medicamento> buscarPorID(Long id);
    Medicamento guardar(Medicamento medicamento);
    void eliminar(Long id);
}
