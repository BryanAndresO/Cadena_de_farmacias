package com.espe.ventas.repositories;

import com.espe.ventas.models.entities.Venta;
import org.springframework.data.repository.CrudRepository;

public interface VentaRepository extends CrudRepository<Venta, Long> {
}

