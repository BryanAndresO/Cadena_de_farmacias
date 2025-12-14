package ec.edu.espe.micro_inventario.dto;

import jakarta.validation.constraints.Min;

public class InventarioUpdateDTO {

    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    // Constructors
    public InventarioUpdateDTO() {
    }

    public InventarioUpdateDTO(Integer stock, Integer stockMinimo) {
        this.stock = stock;
        this.stockMinimo = stockMinimo;
    }

    // Getters and Setters
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
