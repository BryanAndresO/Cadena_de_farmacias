package ec.edu.espe.micro_inventario.service;

import ec.edu.espe.micro_inventario.client.CatalogoClient;
import ec.edu.espe.micro_inventario.dto.InventarioCreateDTO;
import ec.edu.espe.micro_inventario.dto.InventarioDTO;
import ec.edu.espe.micro_inventario.dto.InventarioUpdateDTO;
import ec.edu.espe.micro_inventario.dto.MedicamentoDTO;
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
            // ProductoID is String in Inventario but Long in Catalogo?
            // Assuming for now they match parseable IDs. If not, this logic is skipped or
            // needs adaptation.
            System.err.println("Advertencia: ID de producto no numerico, saltando validacion global: "
                    + createDTO.getProductoID());
        } catch (Exception e) {
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
            // Potentially we could add logic here to return stock to global if reduced,
            // or take more if increased. For simplicity, treating manual update as
            // 'correction'.
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

        // Note: Logic for adjustments usually implies just local change (lost/broken
        // items).
        // If 'Assigned from Warehouse', use separate flow or treat positive adjustment
        // as requiring global deduction.
        // For this specific requirement, let's enforce global deduction ONLY if
        // increasing stock (Assignment logic)

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
