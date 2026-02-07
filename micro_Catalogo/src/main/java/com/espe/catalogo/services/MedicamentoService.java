package com.espe.catalogo.services;

import com.espe.catalogo.dtos.MedicamentoDTO;
import com.espe.catalogo.models.entities.Medicamento;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MedicamentoService {

    List<MedicamentoDTO> buscarTodos();

    Page<MedicamentoDTO> buscarTodos(Pageable pageable);

    Optional<MedicamentoDTO> buscarPorID(Long id);

    MedicamentoDTO guardar(MedicamentoDTO medicamentoDTO);

    void eliminar(Long id);
}
