package com.federico.negocio.app.msvc_acreditaciones.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name ="Acreditaciones")
public class Acreditacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private double importe;
    private int identificadorPuntoVenta;
    private LocalDate fechaPedido;
    private String nombrePuntoventa;
}
