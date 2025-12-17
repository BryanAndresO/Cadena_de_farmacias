package com.espe.catalogo.models.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "medicamentos")
public class Medicamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.validation.constraints.NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @jakarta.validation.constraints.NotBlank(message = "El laboratorio es obligatorio")
    private String laboratorio;

    @jakarta.validation.constraints.NotNull(message = "El precio es obligatorio")
    @jakarta.validation.constraints.Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    @jakarta.validation.constraints.NotNull(message = "El stock es obligatorio")
    @jakarta.validation.constraints.Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}

