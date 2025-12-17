package com.espe.reporte.services;

import com.espe.reporte.models.entities.ReporteVenta;
import com.espe.reporte.dtos.ReporteVentaDTO;
import com.espe.reporte.repositories.ReporteVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Optional;

@Service
public class ReporteVentaServiceImpl implements ReporteVentaService {
    @Autowired
    private ReporteVentaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<ReporteVentaDTO> buscarTodos() {
        List<ReporteVenta> reportes = (List<ReporteVenta>) repository.findAll();
        return reportes.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReporteVentaDTO> buscarPorID(Long id) {
        return repository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional
    public ReporteVentaDTO guardar(ReporteVentaDTO reporteVentaDTO) {
        ReporteVenta reporteVenta = convertToEntity(reporteVentaDTO);
        ReporteVenta reporteGuardado = repository.save(reporteVenta);
        return convertToDTO(reporteGuardado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    private ReporteVentaDTO convertToDTO(ReporteVenta entity) {
        ReporteVentaDTO dto = new ReporteVentaDTO();
        dto.setId(entity.getId());
        dto.setFecha(entity.getFecha());
        dto.setTotalVentas(entity.getTotalVentas());
        dto.setSucursalId(entity.getSucursalId());
        return dto;
    }

    private ReporteVenta convertToEntity(ReporteVentaDTO dto) {
        ReporteVenta entity = new ReporteVenta();
        entity.setId(dto.getId());
        entity.setFecha(dto.getFecha());
        entity.setTotalVentas(dto.getTotalVentas());
        entity.setSucursalId(dto.getSucursalId());
        return entity;
    }
}
