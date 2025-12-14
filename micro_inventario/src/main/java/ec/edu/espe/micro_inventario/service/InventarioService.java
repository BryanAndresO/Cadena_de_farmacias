package ec.edu.espe.micro_inventario.service;

import ec.edu.espe.micro_inventario.dto.InventarioCreateDTO;
import ec.edu.espe.micro_inventario.dto.InventarioDTO;
import ec.edu.espe.micro_inventario.dto.InventarioUpdateDTO;

import java.util.List;

public interface InventarioService {

    /**
     * Get all inventory records
     */
    List<InventarioDTO> findAll();

    /**
     * Get inventory by ID
     */
    InventarioDTO findById(Long id);

    /**
     * Get all inventory for a specific branch
     */
    List<InventarioDTO> findBySucursal(Long sucursalID);

    /**
     * Get all inventory for a specific product
     */
    List<InventarioDTO> findByProducto(String productoID);

    /**
     * Get inventory for a specific product at a specific branch
     */
    InventarioDTO findBySucursalAndProducto(Long sucursalID, String productoID);

    /**
     * Get all low stock items
     */
    List<InventarioDTO> findLowStockItems();

    /**
     * Get low stock items for a specific branch
     */
    List<InventarioDTO> findLowStockItemsBySucursal(Long sucursalID);

    /**
     * Create new inventory record
     */
    InventarioDTO create(InventarioCreateDTO createDTO);

    /**
     * Update existing inventory record
     */
    InventarioDTO update(Long id, InventarioUpdateDTO updateDTO);

    /**
     * Adjust stock (increment or decrement)
     */
    InventarioDTO adjustStock(Long id, Integer adjustment);

    /**
     * Delete inventory record
     */
    void delete(Long id);
}
