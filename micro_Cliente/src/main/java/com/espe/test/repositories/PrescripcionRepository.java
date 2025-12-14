package com.espe.test.repositories;

import com.espe.test.models.entities.Prescripcion;
import org.springframework.data.repository.CrudRepository;

public interface PrescripcionRepository extends CrudRepository<Prescripcion, Long> {
}
