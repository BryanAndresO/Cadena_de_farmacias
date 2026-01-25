package com.espe.inventario.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventario_movimiento")
public class InventarioMovimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sucursal_id", nullable = false)
    private Long sucursalId;

    @Column(name = "producto_id", nullable = false)
    private String productoId;

    @Column(nullable = false)
    private Integer cantidad; // Positivo = entrada/asignación, Negativo = salida/devolución

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoMovimiento tipoMovimiento;

    @Column(nullable = false)
    private LocalDateTime fechaMovimiento;

    @Column(name = "usuario_responsable", length = 100)
    private String usuarioResponsable;

    @Column(length = 500)
    private String observaciones;

    @Column(name = "inventario_id")
    private Long inventarioId; // Referencia al registro de inventario afectado

    @Column(name = "stock_anterior")
    private Integer stockAnterior; // Stock antes del movimiento

    @Column(name = "stock_nuevo")
    private Integer stockNuevo; // Stock después del movimiento

    @Column(name = "stock_global_anterior")
    private Integer stockGlobalAnterior; // Stock del catálogo antes

    @Column(name = "stock_global_nuevo")
    private Integer stockGlobalNuevo; // Stock del catálogo después

    // Constructor por defecto
    public InventarioMovimiento() {
        this.fechaMovimiento = LocalDateTime.now();
    }

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

    public enum TipoMovimiento {
        ASIGNACION_INICIAL,    // Primera asignación a sucursal
        ASIGNACION_ADICIONAL,  // Más stock asignado desde el catálogo
        DEVOLUCION,            // Devolución de sucursal al catálogo
        AJUSTE_INVENTARIO,     // Ajuste manual (merma, corrección, etc.)
        VENTA,                 // Salida por venta
        ENTRADA_COMPRA         // Entrada por compra
    }
}
