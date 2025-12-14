package com.espe.test.services;

import com.espe.test.models.entities.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteService {
    List<Cliente> buscarTodos();
    Optional<Cliente> buscarPorID(Long id);
    Cliente guardar(Cliente cliente);
    void eliminar(Long id);
}
