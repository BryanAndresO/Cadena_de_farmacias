package ec.edu.espe.micro_inventario.service;

import ec.edu.espe.micro_inventario.dto.InventarioCreateDTO;
import ec.edu.espe.micro_inventario.dto.InventarioDTO;
import ec.edu.espe.micro_inventario.dto.InventarioUpdateDTO;
import ec.edu.espe.micro_inventario.exception.DuplicateInventoryException;
import ec.edu.espe.micro_inventario.exception.InvalidStockOperationException;
import ec.edu.espe.micro_inventario.exception.ResourceNotFoundException;
import ec.edu.espe.micro_inventario.model.Inventario;
import ec.edu.espe.micro_inventario.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventarioServiceImpl implements InventarioService {

    @Autowired
    private InventarioRepository repository;

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
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", "id", id));
        return convertToDTO(inventario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioDTO> findBySucursal(Long sucursalID) {
        return repository.findBySucursalID(sucursalID).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioDTO> findByProducto(String productoID) {
        return repository.findByProductoID(productoID).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioDTO findBySucursalAndProducto(Long sucursalID, String productoID) {
        Inventario inventario = repository.findBySucursalIDAndProductoID(sucursalID, productoID)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Inventario no encontrado para sucursal %d y producto %s",
                                sucursalID, productoID)));
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
    public List<InventarioDTO> findLowStockItemsBySucursal(Long sucursalID) {
        return repository.findLowStockItemsBySucursal(sucursalID).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventarioDTO create(InventarioCreateDTO createDTO) {
        // Check if inventory already exists for this product at this branch
        if (repository.findBySucursalIDAndProductoID(
                createDTO.getSucursalID(),
                createDTO.getProductoID()).isPresent()) {
            throw new DuplicateInventoryException(
                    createDTO.getSucursalID(),
                    createDTO.getProductoID());
        }

        Inventario inventario = new Inventario();
        inventario.setSucursalID(createDTO.getSucursalID());
        inventario.setProductoID(createDTO.getProductoID());
        inventario.setStock(createDTO.getStock());
        inventario.setStockMinimo(createDTO.getStockMinimo());

        Inventario saved = repository.save(inventario);
        return convertToDTO(saved);
    }

    @Override
    public InventarioDTO update(Long id, InventarioUpdateDTO updateDTO) {
        Inventario inventario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", "id", id));

        // Update only provided fields
        if (updateDTO.getStock() != null) {
            if (updateDTO.getStock() < 0) {
                throw new InvalidStockOperationException("El stock no puede ser negativo");
            }
            inventario.setStock(updateDTO.getStock());
        }

        if (updateDTO.getStockMinimo() != null) {
            if (updateDTO.getStockMinimo() < 0) {
                throw new InvalidStockOperationException("El stock mínimo no puede ser negativo");
            }
            inventario.setStockMinimo(updateDTO.getStockMinimo());
        }

        Inventario updated = repository.save(inventario);
        return convertToDTO(updated);
    }

    @Override
    public InventarioDTO adjustStock(Long id, Integer adjustment) {
        Inventario inventario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", "id", id));

        int newStock = inventario.getStock() + adjustment;

        if (newStock < 0) {
            throw new InvalidStockOperationException(
                    String.format("Stock insuficiente. Stock actual: %d, Ajuste solicitado: %d",
                            inventario.getStock(), adjustment));
        }

        inventario.setStock(newStock);
        Inventario updated = repository.save(inventario);
        return convertToDTO(updated);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Inventario", "id", id);
        }
        repository.deleteById(id);
    }

    /**
     * Convert Entity to DTO
     */
    private InventarioDTO convertToDTO(Inventario inventario) {
        return new InventarioDTO(
                inventario.getId(),
                inventario.getSucursalID(),
                inventario.getProductoID(),
                inventario.getStock(),
                inventario.getStockMinimo());
    }
}
