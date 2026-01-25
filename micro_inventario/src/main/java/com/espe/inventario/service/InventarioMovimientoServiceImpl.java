package com.espe.inventario.service;

import com.espe.inventario.dto.InventarioMovimientoDTO;
import com.espe.inventario.model.InventarioMovimiento;
import com.espe.inventario.repository.InventarioMovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventarioMovimientoServiceImpl implements InventarioMovimientoService {

    @Autowired
    private InventarioMovimientoRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<InventarioMovimientoDTO> findAll() {
        return repository.findAllByOrderByFechaMovimientoDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioMovimientoDTO> findBySucursal(Long sucursalId) {
        return repository.findBySucursalIdOrderByFechaMovimientoDesc(sucursalId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioMovimientoDTO> findByProducto(String productoId) {
        return repository.findByProductoIdOrderByFechaMovimientoDesc(productoId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioMovimientoDTO> findBySucursalAndProducto(Long sucursalId, String productoId) {
        return repository.findBySucursalIdAndProductoIdOrderByFechaMovimientoDesc(sucursalId, productoId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioMovimientoDTO> findByInventario(Long inventarioId) {
        return repository.findByInventarioIdOrderByFechaMovimientoDesc(inventarioId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private InventarioMovimientoDTO convertToDTO(InventarioMovimiento entity) {
        InventarioMovimientoDTO dto = new InventarioMovimientoDTO();
        dto.setId(entity.getId());
        dto.setSucursalId(entity.getSucursalId());
        dto.setProductoId(entity.getProductoId());
        dto.setCantidad(entity.getCantidad());
        dto.setTipoMovimiento(entity.getTipoMovimiento());
        dto.setFechaMovimiento(entity.getFechaMovimiento());
        dto.setUsuarioResponsable(entity.getUsuarioResponsable());
        dto.setObservaciones(entity.getObservaciones());
        dto.setInventarioId(entity.getInventarioId());
        dto.setStockAnterior(entity.getStockAnterior());
        dto.setStockNuevo(entity.getStockNuevo());
        dto.setStockGlobalAnterior(entity.getStockGlobalAnterior());
        dto.setStockGlobalNuevo(entity.getStockGlobalNuevo());
        return dto;
    }
}
