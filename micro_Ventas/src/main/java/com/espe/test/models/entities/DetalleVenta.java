package com.espe.test.models.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_ventas")
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @jakarta.validation.constraints.NotNull(message = "El ID de la venta es obligatorio")
    private Long ventaId;

    @jakarta.validation.constraints.NotNull(message = "El ID del medicamento es obligatorio")
    private Long medicamentoId;

    @jakarta.validation.constraints.NotNull(message = "La cantidad es obligatoria")
    @jakarta.validation.constraints.Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @jakarta.validation.constraints.NotNull(message = "El precio unitario es obligatorio")
    @jakarta.validation.constraints.Positive(message = "El precio unitario debe ser mayor a 0")
    private Double precioUnitario;

    // getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }

    public Long getMedicamentoId() {
        return medicamentoId;
    }

    public void setMedicamentoId(Long medicamentoId) {
        this.medicamentoId = medicamentoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
