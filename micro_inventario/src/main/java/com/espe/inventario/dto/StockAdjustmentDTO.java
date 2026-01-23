package com.espe.inventario.dto;

import jakarta.validation.constraints.NotNull;

public class StockAdjustmentDTO {

    @NotNull(message = "El ajuste de stock es obligatorio")
    private Integer adjustment;

    // Constructors
    public StockAdjustmentDTO() {
    }

    public StockAdjustmentDTO(Integer adjustment) {
        this.adjustment = adjustment;
    }

    // Getters and Setters
    public Integer getAdjustment() {
        return adjustment;
    }

    public void setAdjustment(Integer adjustment) {
        this.adjustment = adjustment;
    }
}
