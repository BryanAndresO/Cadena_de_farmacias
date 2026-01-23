package com.espe.inventario.service;

import com.espe.inventario.client.CatalogoClient;
import com.espe.inventario.dto.InventarioCreateDTO;
import com.espe.inventario.dto.InventarioDTO;
import com.espe.inventario.dto.InventarioUpdateDTO;
import com.espe.inventario.dto.MedicamentoDTO;
import com.espe.inventario.exceptions.ResourceNotFoundException;
import com.espe.inventario.model.Inventario;
import com.espe.inventario.repository.InventarioRepository;
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
        try {
            Long prodId = Long.parseLong(createDTO.getProductoID());
            MedicamentoDTO medicamento = catalogoClient.findById(prodId);

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

        Inventario entity = new Inventario();
        entity.setSucursalID(createDTO.getSucursalID());
        entity.setProductoID(createDTO.getProductoID());
        entity.setStock(createDTO.getStock());
        entity.setStockMinimo(createDTO.getStockMinimo());

        return convertToDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public InventarioDTO update(Long id, InventarioUpdateDTO updateDTO) {
        Inventario entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado con id: " + id));

        if (updateDTO.getStock() != null) {
            entity.setStock(updateDTO.getStock());
        }
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

        if (adjustment > 0) {
            try {
                Long prodId = Long.parseLong(entity.getProductoID());
                MedicamentoDTO medicamento = catalogoClient.findById(prodId);

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

        int newStock = entity.getStock() + adjustment;
        if (newStock < 0) {
            throw new RuntimeException("Stock insuficiente para realizar el ajuste");
        }
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
