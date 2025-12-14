package com.espe.test.repositories;

import com.espe.test.models.entities.Sucursal;
import org.springframework.data.repository.CrudRepository;

public interface SucursalRepository extends CrudRepository<Sucursal, Long> {
}
