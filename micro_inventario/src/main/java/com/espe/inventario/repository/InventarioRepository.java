package com.espe.inventario.repository;

import com.espe.inventario.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    /**
     * Find all inventory records for a specific branch
     */
    List<Inventario> findBySucursalID(Long sucursalID);

    /**
     * Find all inventory records for a specific product
     */
    List<Inventario> findByProductoID(String productoID);

    /**
     * Find inventory record for a specific product at a specific branch
     */
    Optional<Inventario> findBySucursalIDAndProductoID(Long sucursalID, String productoID);

    /**
     * Find all inventory records where current stock is below minimum stock
     */
    @Query("SELECT i FROM Inventario i WHERE i.stock < i.stockMinimo")
    List<Inventario> findLowStockItems();

    /**
     * Find low stock items for a specific branch
     */
    @Query("SELECT i FROM Inventario i WHERE i.sucursalID = :sucursalID AND i.stock < i.stockMinimo")
    List<Inventario> findLowStockItemsBySucursal(Long sucursalID);
}
