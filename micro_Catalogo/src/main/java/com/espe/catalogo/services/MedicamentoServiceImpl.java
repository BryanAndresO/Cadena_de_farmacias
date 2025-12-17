package com.espe.catalogo.services;

import com.espe.catalogo.dtos.MedicamentoDTO;
import com.espe.catalogo.models.entities.Medicamento;
import com.espe.catalogo.repositories.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicamentoServiceImpl implements MedicamentoService {

    @Autowired
    private MedicamentoRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<MedicamentoDTO> buscarTodos() {
        return ((List<Medicamento>) repository.findAll()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MedicamentoDTO> buscarPorID(Long id) {
        return repository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional
    public MedicamentoDTO guardar(MedicamentoDTO medicamentoDTO) {
        Medicamento entity = convertToEntity(medicamentoDTO);
        Medicamento savedEntity = repository.save(entity);
        return convertToDTO(savedEntity);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    private MedicamentoDTO convertToDTO(Medicamento entity) {
        MedicamentoDTO dto = new MedicamentoDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setLaboratorio(entity.getLaboratorio());
        if (entity.getPrecio() != null) {
            dto.setPrecio(BigDecimal.valueOf(entity.getPrecio()));
        }
        dto.setStock(entity.getStock());
        return dto;
    }

    private Medicamento convertToEntity(MedicamentoDTO dto) {
        Medicamento entity = new Medicamento();
        entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setLaboratorio(dto.getLaboratorio());
        if (dto.getPrecio() != null) {
            entity.setPrecio(dto.getPrecio().doubleValue());
        }
        entity.setStock(dto.getStock());
        return entity;
    }
}
