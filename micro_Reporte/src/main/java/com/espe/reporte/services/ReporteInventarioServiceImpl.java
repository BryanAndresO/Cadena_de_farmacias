package com.espe.reporte.services;

import com.espe.reporte.models.entities.ReporteInventario;
import com.espe.reporte.dtos.ReporteInventarioDTO;
import com.espe.reporte.repositories.ReporteInventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Optional;

@Service
public class ReporteInventarioServiceImpl implements ReporteInventarioService {
    @Autowired
    private ReporteInventarioRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<ReporteInventarioDTO> buscarTodos() {
        List<ReporteInventario> reportes = (List<ReporteInventario>) repository.findAll();
        return reportes.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReporteInventarioDTO> buscarPorID(Long id) {
        return repository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional
    public ReporteInventarioDTO guardar(ReporteInventarioDTO reporteInventarioDTO) {
        ReporteInventario reporte = convertToEntity(reporteInventarioDTO);
        ReporteInventario reporteGuardado = repository.save(reporte);
        return convertToDTO(reporteGuardado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    private ReporteInventarioDTO convertToDTO(ReporteInventario entity) {
        ReporteInventarioDTO dto = new ReporteInventarioDTO();
        dto.setId(entity.getId());
        dto.setFecha(entity.getFecha());
        dto.setSucursalId(entity.getSucursalId());
        dto.setMedicamentoId(entity.getMedicamentoId());
        dto.setStock(entity.getStock());
        return dto;
    }

    private ReporteInventario convertToEntity(ReporteInventarioDTO dto) {
        ReporteInventario entity = new ReporteInventario();
        entity.setId(dto.getId());
        entity.setFecha(dto.getFecha());
        entity.setSucursalId(dto.getSucursalId());
        entity.setMedicamentoId(dto.getMedicamentoId());
        entity.setStock(dto.getStock());
        return entity;
    }
}
