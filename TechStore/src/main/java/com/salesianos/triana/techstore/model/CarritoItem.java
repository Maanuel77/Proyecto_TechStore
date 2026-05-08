package com.salesianos.triana.techstore.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CarritoItem implements Serializable {

    private static final double COSTE_GARANTIA_PORCENTAJE = 0.10;

    private Long    productoId;
    private String  nombre;
    private Double  precio;
    private Integer cantidad;
    private boolean garantiaExtendida = false;
    private Integer garantiaMeses;

    public CarritoItem(Long productoId, String nombre, Double precio, Integer cantidad, Integer garantiaMeses) {
        this.productoId    = productoId;
        this.nombre        = nombre;
        this.precio        = precio;
        this.cantidad      = cantidad;
        this.garantiaMeses = garantiaMeses;
    }

    public Double getSubtotal() {
        return precio * cantidad;
    }

    /** Coste de la garantía extendida: 10 % del precio por unidad. */
    public Double getCosteGarantia() {
        return garantiaExtendida ? precio * cantidad * COSTE_GARANTIA_PORCENTAJE : 0.0;
    }
}
