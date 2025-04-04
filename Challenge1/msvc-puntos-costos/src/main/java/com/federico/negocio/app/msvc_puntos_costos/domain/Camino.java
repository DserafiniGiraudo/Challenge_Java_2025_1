package com.federico.negocio.app.msvc_puntos_costos.domain;

import lombok.Data;

@Data
public class Camino {

    CaminoPK caminoPK;
    private int costo;

    public Camino(CaminoPK caminoPK,int costo) {
        validarCosto(costo);
        this.caminoPK = caminoPK;
        this.costo = costo;
    }

    public Camino caminoInverso() {
        return new Camino(new CaminoPK(caminoPK.getPuntoB(),caminoPK.getPuntoA()),costo);
    }

    private void validarCosto(int costo) {
        if  (costo < 0) {
            throw new RuntimeException("El costo no puede ser negativo");
        }
    }

}
