package com.federico.negocio.app.msvc_acreditaciones.domain;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document(collection = "acreditacionesV2")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Acreditacion {

    @Id
    private String id;
    private double importe;
    @Field("idPuntoVenta")
    private int identificadorPuntoVenta;
    @Field("fechaRecepcion")
    private LocalDate fechaPedido;
    private String nombrePuntoventa;
}
