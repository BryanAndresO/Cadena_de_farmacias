package ec.edu.espe.micro_inventario.service;

import ec.edu.espe.micro_inventario.dto.InventarioCreateDTO;
import ec.edu.espe.micro_inventario.dto.InventarioDTO;
import ec.edu.espe.micro_inventario.dto.InventarioUpdateDTO;
import java.util.List;

public interface InventarioService {
    List<InventarioDTO> findAll();

    InventarioDTO findById(Long id);

    List<InventarioDTO> findBySucursal(Long sucursalId);

    List<InventarioDTO> findByProducto(String productoId);

    InventarioDTO findBySucursalAndProducto(Long sucursalId, String productoId);

    List<InventarioDTO> findLowStockItems();

    List<InventarioDTO> findLowStockItemsBySucursal(Long sucursalId);

    InventarioDTO create(InventarioCreateDTO createDTO);

    InventarioDTO update(Long id, InventarioUpdateDTO updateDTO);

    InventarioDTO adjustStock(Long id, Integer adjustment);

    void delete(Long id);
}
