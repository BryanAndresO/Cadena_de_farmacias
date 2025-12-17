package com.espe.cliente.services;

import com.espe.cliente.dtos.ClienteDTO;
import com.espe.cliente.models.entities.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteService {
    List<ClienteDTO> buscarTodos();

    Optional<ClienteDTO> buscarPorID(Long id);

    ClienteDTO guardar(ClienteDTO clienteDTO);

    void eliminar(Long id);
}
