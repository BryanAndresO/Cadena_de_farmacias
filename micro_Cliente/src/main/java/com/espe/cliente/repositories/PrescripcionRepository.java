package com.espe.cliente.repositories;

import com.espe.cliente.models.entities.Prescripcion;
import org.springframework.data.repository.CrudRepository;

public interface PrescripcionRepository extends CrudRepository<Prescripcion, Long> {
}

