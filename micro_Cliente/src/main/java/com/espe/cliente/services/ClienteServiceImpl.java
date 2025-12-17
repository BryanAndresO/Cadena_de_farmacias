package com.espe.cliente.services;

import com.espe.cliente.dtos.ClienteDTO;
import com.espe.cliente.models.entities.Cliente;
import com.espe.cliente.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> buscarTodos() {
        return ((List<Cliente>) repository.findAll()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteDTO> buscarPorID(Long id) {
        return repository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional
    public ClienteDTO guardar(ClienteDTO clienteDTO) {
        Cliente entity = convertToEntity(clienteDTO);
        Cliente savedEntity = repository.save(entity);
        return convertToDTO(savedEntity);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    private ClienteDTO convertToDTO(Cliente entity) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setCedula(entity.getCedula());
        dto.setDireccion(entity.getDireccion());
        dto.setTelefono(entity.getTelefono());
        dto.setEmail(entity.getEmail());
        return dto;
    }

    private Cliente convertToEntity(ClienteDTO dto) {
        Cliente entity = new Cliente();
        entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setCedula(dto.getCedula());
        entity.setDireccion(dto.getDireccion());
        entity.setTelefono(dto.getTelefono());
        entity.setEmail(dto.getEmail());
        return entity;
    }
}
