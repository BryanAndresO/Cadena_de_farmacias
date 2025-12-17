package com.espe.reporte.models.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "reporte_ventas")
public class ReporteVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fecha;
    private Double totalVentas;
    private Long sucursalId;

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public Double getTotalVentas() { return totalVentas; }
    public void setTotalVentas(Double totalVentas) { this.totalVentas = totalVentas; }
    public Long getSucursalId() { return sucursalId; }
    public void setSucursalId(Long sucursalId) { this.sucursalId = sucursalId; }
}

