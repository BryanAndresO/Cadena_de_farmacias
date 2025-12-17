package com.espe.cliente.models.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "prescripciones")
public class Prescripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @jakarta.validation.constraints.NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
    private String fecha;

    @jakarta.validation.constraints.NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @jakarta.validation.constraints.NotNull(message = "El ID del medicamento es obligatorio")
    private Long medicamentoId;

    // getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getMedicamentoId() {
        return medicamentoId;
    }

    public void setMedicamentoId(Long medicamentoId) {
        this.medicamentoId = medicamentoId;
    }
}

