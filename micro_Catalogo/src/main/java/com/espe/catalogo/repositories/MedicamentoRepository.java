package com.espe.catalogo.repositories;

import com.espe.catalogo.models.entities.Medicamento;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

}
