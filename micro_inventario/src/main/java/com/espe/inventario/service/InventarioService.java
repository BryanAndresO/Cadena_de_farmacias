package com.espe.inventario.service;

import com.espe.inventario.dto.InventarioCreateDTO;
import com.espe.inventario.dto.InventarioDTO;
import com.espe.inventario.dto.InventarioUpdateDTO;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventarioService {
    List<InventarioDTO> findAll();

    Page<InventarioDTO> findAll(Pageable pageable);

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
