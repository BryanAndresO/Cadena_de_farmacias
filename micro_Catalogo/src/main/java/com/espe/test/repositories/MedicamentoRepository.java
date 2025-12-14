package com.espe.test.repositories;

import com.espe.test.models.entities.Medicamento;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface MedicamentoRepository extends CrudRepository<Medicamento, Long> {

}
