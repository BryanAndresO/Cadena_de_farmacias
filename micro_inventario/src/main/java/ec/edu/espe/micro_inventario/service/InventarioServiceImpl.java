package ec.edu.espe.micro_inventario.service;

import ec.edu.espe.micro_inventario.dto.InventarioCreateDTO;
import ec.edu.espe.micro_inventario.dto.InventarioDTO;
import ec.edu.espe.micro_inventario.dto.InventarioUpdateDTO;
import ec.edu.espe.micro_inventario.exceptions.ResourceNotFoundException;
import ec.edu.espe.micro_inventario.model.Inventario;
import ec.edu.espe.micro_inventario.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
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
            throw new RuntimeException("El producto ya existe en esta sucursal"); // Or custom exception
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
