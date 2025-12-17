package com.espe.sucursal.models.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "sucursales")
public class Sucursal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @jakarta.validation.constraints.NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @jakarta.validation.constraints.NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    @jakarta.validation.constraints.NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @jakarta.validation.constraints.NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

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

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}

