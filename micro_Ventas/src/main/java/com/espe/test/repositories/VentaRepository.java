package com.espe.test.repositories;

import com.espe.test.models.entities.Venta;
import org.springframework.data.repository.CrudRepository;

public interface VentaRepository extends CrudRepository<Venta, Long> {
}
