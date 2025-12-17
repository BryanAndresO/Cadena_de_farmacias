package com.espe.catalogo.repositories;

import com.espe.catalogo.models.entities.Medicamento;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface MedicamentoRepository extends CrudRepository<Medicamento, Long> {

}

