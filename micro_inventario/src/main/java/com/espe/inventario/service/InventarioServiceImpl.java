package com.espe.inventario.service;

import com.espe.inventario.client.CatalogoClient;
import com.espe.inventario.dto.InventarioCreateDTO;
import com.espe.inventario.dto.InventarioDTO;
import com.espe.inventario.dto.InventarioUpdateDTO;
import com.espe.inventario.dto.MedicamentoDTO;
import com.espe.inventario.exceptions.ResourceNotFoundException;
import com.espe.inventario.model.Inventario;
import com.espe.inventario.model.InventarioMovimiento;
import com.espe.inventario.model.InventarioMovimiento.TipoMovimiento;
import com.espe.inventario.repository.InventarioRepository;
import com.espe.inventario.repository.InventarioMovimientoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventarioServiceImpl implements InventarioService {

    private static final Logger log = LoggerFactory.getLogger(InventarioServiceImpl.class);

    @Autowired
    private InventarioRepository repository;

    @Autowired
    private InventarioMovimientoRepository movimientoRepository;

    @Autowired
    private CatalogoClient catalogoClient;

    @Override
    @Transactional(readOnly = true)
    public List<InventarioDTO> findAll() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioDTO findById(Long id) {
        Inventario inventario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado con id: " + id));
        return convertToDTO(inventario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioDTO> findBySucursal(Long sucursalId) {
        return repository.findBySucursalID(sucursalId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioDTO> findByProducto(String productoId) {
        return repository.findByProductoID(productoId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioDTO findBySucursalAndProducto(Long sucursalId, String productoId) {
        Inventario inventario = repository.findBySucursalIDAndProductoID(sucursalId, productoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventario no encontrado para sucursal " + sucursalId + " y producto " + productoId));
        return convertToDTO(inventario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioDTO> findLowStockItems() {
        return repository.findLowStockItems().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioDTO> findLowStockItemsBySucursal(Long sucursalId) {
        return repository.findLowStockItemsBySucursal(sucursalId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InventarioDTO create(InventarioCreateDTO createDTO) {
        // Validation: verify if already exists
        if (repository.findBySucursalIDAndProductoID(createDTO.getSucursalID(), createDTO.getProductoID())
                .isPresent()) {
            throw new RuntimeException("El producto ya existe en esta sucursal");
        }

        // GLOBAL STOCK VALIDATION
        MedicamentoDTO medicamento = null;
        int stockGlobalAnterior = 0;
        try {
            Long prodId = Long.parseLong(createDTO.getProductoID());
            medicamento = catalogoClient.findById(prodId);
            stockGlobalAnterior = medicamento.getStock();

            if (medicamento.getStock() < createDTO.getStock()) {
                throw new RuntimeException(
                        String.format("Stock insuficiente en Catálogo General. Disponible: %d, Solicitado: %d",
                                medicamento.getStock(), createDTO.getStock()));
            }

            // Deduct from Global (Catalogo)
            medicamento.setStock(medicamento.getStock() - createDTO.getStock());
            catalogoClient.update(prodId, medicamento);

        } catch (NumberFormatException e) {
            log.warn("ID de producto no numerico, saltando validacion global: {}", createDTO.getProductoID());
        } catch (Exception e) {
            log.error("Error al validar stock con micro-catalogo: {}", e.getMessage(), e);
            throw new RuntimeException("Error al validar stock con micro-catalogo: " + e.getMessage());
        }

        // Create inventory entity
        Inventario entity = new Inventario();
        entity.setSucursalID(createDTO.getSucursalID());
        entity.setProductoID(createDTO.getProductoID());
        entity.setStock(createDTO.getStock());
        entity.setStockMinimo(createDTO.getStockMinimo());
        
        Inventario savedEntity = repository.save(entity);

        // REGISTER TRANSACTION IN HISTORY
        InventarioMovimiento movimiento = new InventarioMovimiento();
        movimiento.setSucursalId(savedEntity.getSucursalID());
        movimiento.setProductoId(savedEntity.getProductoID());
        movimiento.setCantidad(createDTO.getStock());
        movimiento.setTipoMovimiento(TipoMovimiento.ASIGNACION_INICIAL);
        movimiento.setInventarioId(savedEntity.getId());
        movimiento.setStockAnterior(0);
        movimiento.setStockNuevo(createDTO.getStock());
        if (medicamento != null) {
            movimiento.setStockGlobalAnterior(stockGlobalAnterior);
            movimiento.setStockGlobalNuevo(medicamento.getStock());
        }
        movimiento.setObservaciones("Asignación inicial de stock a sucursal");
        
        movimientoRepository.save(movimiento);

        return convertToDTO(savedEntity);
    }

    @Override
    @Transactional
    public InventarioDTO update(Long id, InventarioUpdateDTO updateDTO) {
        Inventario entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado con id: " + id));

        int stockAnterior = entity.getStock();
        MedicamentoDTO medicamento = null;
        int stockGlobalAnterior = 0;

        // CRITICAL: Si se está cambiando el stock, NO MODIFICAR directamente
        // En su lugar, calcular la diferencia y crear una nueva transacción
        if (updateDTO.getStock() != null && !updateDTO.getStock().equals(stockAnterior)) {
            int diferencia = updateDTO.getStock() - stockAnterior;
            
            // Si diferencia es positiva: se está asignando más (debe validar stock global)
            // Si diferencia es negativa: se está devolviendo al catálogo
            
            try {
                Long prodId = Long.parseLong(entity.getProductoID());
                medicamento = catalogoClient.findById(prodId);
                stockGlobalAnterior = medicamento.getStock();

                if (diferencia > 0) {
                    // ASIGNACIÓN ADICIONAL: validar que haya stock disponible
                    if (medicamento.getStock() < diferencia) {
                        throw new RuntimeException(
                                String.format("Stock insuficiente en Catálogo General. Disponible: %d, Requerido: %d",
                                        medicamento.getStock(), diferencia));
                    }
                    // Deducir del catálogo
                    medicamento.setStock(medicamento.getStock() - diferencia);
                    catalogoClient.update(prodId, medicamento);
                    
                } else if (diferencia < 0) {
                    // DEVOLUCIÓN: regresar al catálogo
                    medicamento.setStock(medicamento.getStock() + Math.abs(diferencia));
                    catalogoClient.update(prodId, medicamento);
                }

            } catch (NumberFormatException e) {
                log.warn("ID de producto no numerico, saltando validacion global: {}", entity.getProductoID());
            } catch (Exception e) {
                log.error("Error al validar stock con micro-catalogo: {}", e.getMessage(), e);
                throw new RuntimeException("Error al validar stock con micro-catalogo: " + e.getMessage());
            }

            // REGISTER TRANSACTION IN HISTORY
            InventarioMovimiento movimiento = new InventarioMovimiento();
            movimiento.setSucursalId(entity.getSucursalID());
            movimiento.setProductoId(entity.getProductoID());
            movimiento.setCantidad(diferencia);
            movimiento.setTipoMovimiento(diferencia > 0 ? TipoMovimiento.ASIGNACION_ADICIONAL : TipoMovimiento.DEVOLUCION);
            movimiento.setInventarioId(entity.getId());
            movimiento.setStockAnterior(stockAnterior);
            movimiento.setStockNuevo(updateDTO.getStock());
            if (medicamento != null) {
                movimiento.setStockGlobalAnterior(stockGlobalAnterior);
                movimiento.setStockGlobalNuevo(medicamento.getStock());
            }
            movimiento.setObservaciones(diferencia > 0 ? 
                "Asignación adicional de stock" : 
                "Devolución de stock al catálogo general");
            
            movimientoRepository.save(movimiento);

            // Actualizar el stock en la entidad
            entity.setStock(updateDTO.getStock());
        }

        // Actualizar stock mínimo (esto sí puede modificarse directamente)
        if (updateDTO.getStockMinimo() != null) {
            entity.setStockMinimo(updateDTO.getStockMinimo());
        }

        return convertToDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public InventarioDTO adjustStock(Long id, Integer adjustment) {
        Inventario entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado con id: " + id));

        int stockAnterior = entity.getStock();
        MedicamentoDTO medicamento = null;
        int stockGlobalAnterior = 0;

        // Solo ajustes positivos deben validar stock global
        if (adjustment > 0) {
            try {
                Long prodId = Long.parseLong(entity.getProductoID());
                medicamento = catalogoClient.findById(prodId);
                stockGlobalAnterior = medicamento.getStock();

                if (medicamento.getStock() < adjustment) {
                    throw new RuntimeException(
                            String.format(
                                    "Stock insuficiente en Catálogo General para el incremento. Disponible: %d, Solicitado: %d",
                                    medicamento.getStock(), adjustment));
                }

                // Deduct from Global
                medicamento.setStock(medicamento.getStock() - adjustment);
                catalogoClient.update(prodId, medicamento);

            } catch (Exception e) {
                log.error("Error al validar stock con micro-catalogo durante ajuste: {}", e.getMessage(), e);
                throw new RuntimeException("Error al validar stock con micro-catalogo: " + e.getMessage());
            }
        }

        // Validar que el stock no quede negativo
        int newStock = entity.getStock() + adjustment;
        if (newStock < 0) {
            throw new RuntimeException("Stock insuficiente para realizar el ajuste. Stock actual: " + entity.getStock() + ", Ajuste solicitado: " + adjustment);
        }

        // REGISTER TRANSACTION IN HISTORY
        InventarioMovimiento movimiento = new InventarioMovimiento();
        movimiento.setSucursalId(entity.getSucursalID());
        movimiento.setProductoId(entity.getProductoID());
        movimiento.setCantidad(adjustment);
        movimiento.setTipoMovimiento(TipoMovimiento.AJUSTE_INVENTARIO);
        movimiento.setInventarioId(entity.getId());
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockNuevo(newStock);
        if (medicamento != null) {
            movimiento.setStockGlobalAnterior(stockGlobalAnterior);
            movimiento.setStockGlobalNuevo(medicamento.getStock());
        }
        movimiento.setObservaciones("Ajuste de inventario: " + (adjustment > 0 ? "+" : "") + adjustment);
        
        movimientoRepository.save(movimiento);

        entity.setStock(newStock);
        return convertToDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Inventario no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    private InventarioDTO convertToDTO(Inventario entity) {
        InventarioDTO dto = new InventarioDTO();
        dto.setId(entity.getId());
        dto.setSucursalId(entity.getSucursalID());
        dto.setProductoId(entity.getProductoID());
        dto.setCantidad(entity.getStock());
        dto.setStockMinimo(entity.getStockMinimo());
        return dto;
    }
}
