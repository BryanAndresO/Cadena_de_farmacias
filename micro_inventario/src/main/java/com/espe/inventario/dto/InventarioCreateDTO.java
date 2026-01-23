package com.espe.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InventarioCreateDTO {

    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Long sucursalID;

    @NotNull(message = "El ID del producto es obligatorio")
    private String productoID;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    // Constructors
    public InventarioCreateDTO() {
    }

    public InventarioCreateDTO(Long sucursalID, String productoID, Integer stock, Integer stockMinimo) {
        this.sucursalID = sucursalID;
        this.productoID = productoID;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
    }

    // Getters and Setters
    public Long getSucursalID() {
        return sucursalID;
    }

    public void setSucursalID(Long sucursalID) {
        this.sucursalID = sucursalID;
    }

    public String getProductoID() {
        return productoID;
    }

    public void setProductoID(String productoID) {
        this.productoID = productoID;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }
}
