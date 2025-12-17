package com.espe.catalogo.services;

import com.espe.catalogo.dtos.MedicamentoDTO;
import com.espe.catalogo.models.entities.Medicamento;

import java.util.List;
import java.util.Optional;

public interface MedicamentoService {

    List<MedicamentoDTO> buscarTodos();

    Optional<MedicamentoDTO> buscarPorID(Long id);

    MedicamentoDTO guardar(MedicamentoDTO medicamentoDTO);

    void eliminar(Long id);
}
