package com.espe.cliente.repositories;

import com.espe.cliente.models.entities.Cliente;
import org.springframework.data.repository.CrudRepository;

public interface ClienteRepository extends CrudRepository<Cliente, Long> {
}

