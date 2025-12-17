package com.espe.sucursal.services;

import com.espe.sucursal.dtos.SucursalDTO;
import com.espe.sucursal.models.entities.Sucursal;
import com.espe.sucursal.repositories.SucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SucursalServiceImpl implements SucursalService {

    @Autowired
    private SucursalRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<SucursalDTO> buscarTodos() {
        return ((List<Sucursal>) repository.findAll()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SucursalDTO> buscarPorID(Long id) {
        return repository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional
    public SucursalDTO guardar(SucursalDTO sucursalDTO) {
        Sucursal entity = convertToEntity(sucursalDTO);
        Sucursal savedEntity = repository.save(entity);
        return convertToDTO(savedEntity);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    private SucursalDTO convertToDTO(Sucursal entity) {
        SucursalDTO dto = new SucursalDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setCiudad(entity.getCiudad());
        dto.setDireccion(entity.getDireccion());
        dto.setTelefono(entity.getTelefono());
        return dto;
    }

    private Sucursal convertToEntity(SucursalDTO dto) {
        Sucursal entity = new Sucursal();
        entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setCiudad(dto.getCiudad());
        entity.setDireccion(dto.getDireccion());
        entity.setTelefono(dto.getTelefono());
        return entity;
    }
}
