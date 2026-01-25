package com.espe.inventario.dto;

import com.espe.inventario.model.InventarioMovimiento.TipoMovimiento;
import java.io.Serializable;
import java.time.LocalDateTime;

public class InventarioMovimientoDTO implements Serializable {

    private Long id;
    private Long sucursalId;
    private String productoId;
    private Integer cantidad;
    private TipoMovimiento tipoMovimiento;
    private LocalDateTime fechaMovimiento;
    private String usuarioResponsable;
    private String observaciones;
    private Long inventarioId;
    private Integer stockAnterior;
    private Integer stockNuevo;
    private Integer stockGlobalAnterior;
    private Integer stockGlobalNuevo;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Long sucursalId) {
        this.sucursalId = sucursalId;
    }

    public String getProductoId() {
        return productoId;
    }

    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(LocalDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public void setUsuarioResponsable(String usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Long getInventarioId() {
        return inventarioId;
    }

    public void setInventarioId(Long inventarioId) {
        this.inventarioId = inventarioId;
    }

    public Integer getStockAnterior() {
        return stockAnterior;
    }

    public void setStockAnterior(Integer stockAnterior) {
        this.stockAnterior = stockAnterior;
    }

    public Integer getStockNuevo() {
        return stockNuevo;
    }

    public void setStockNuevo(Integer stockNuevo) {
        this.stockNuevo = stockNuevo;
    }

    public Integer getStockGlobalAnterior() {
        return stockGlobalAnterior;
    }

    public void setStockGlobalAnterior(Integer stockGlobalAnterior) {
        this.stockGlobalAnterior = stockGlobalAnterior;
    }

    public Integer getStockGlobalNuevo() {
        return stockGlobalNuevo;
    }

    public void setStockGlobalNuevo(Integer stockGlobalNuevo) {
        this.stockGlobalNuevo = stockGlobalNuevo;
    }
}
