package com.espe.ventas.models.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "ventas")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fecha;

    @jakarta.validation.constraints.NotNull(message = "El total es obligatorio")
    @jakarta.validation.constraints.PositiveOrZero(message = "El total no puede ser negativo")
    private Double total;

    @jakarta.validation.constraints.NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @jakarta.validation.constraints.NotNull(message = "El ID de la sucursal es obligatorio")
    private Long sucursalId;

    // getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Long sucursalId) {
        this.sucursalId = sucursalId;
    }
}

