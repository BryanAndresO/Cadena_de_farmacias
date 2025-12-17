package com.espe.ventas.services;

import com.espe.ventas.models.entities.Venta;
import com.espe.ventas.dtos.VentaDTO;
import com.espe.ventas.repositories.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Optional;

@Service
public class VentaServiceImpl implements VentaService {
    @Autowired
    private VentaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<VentaDTO> buscarTodos() {
        List<Venta> ventas = (List<Venta>) repository.findAll();
        return ventas.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VentaDTO> buscarPorID(Long id) {
        return repository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional
    public VentaDTO guardar(VentaDTO ventaDTO) {
        Venta venta = convertToEntity(ventaDTO);
        Venta ventaGuardada = repository.save(venta);
        return convertToDTO(ventaGuardada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    private VentaDTO convertToDTO(Venta venta) {
        VentaDTO dto = new VentaDTO();
        dto.setId(venta.getId());
        dto.setFecha(venta.getFecha());
        dto.setTotal(venta.getTotal());
        dto.setClienteId(venta.getClienteId());
        dto.setSucursalId(venta.getSucursalId());
        return dto;
    }

    private Venta convertToEntity(VentaDTO dto) {
        Venta venta = new Venta();
        venta.setId(dto.getId());
        venta.setFecha(dto.getFecha());
        venta.setTotal(dto.getTotal());
        venta.setClienteId(dto.getClienteId());
        venta.setSucursalId(dto.getSucursalId());
        return venta;
    }
}
