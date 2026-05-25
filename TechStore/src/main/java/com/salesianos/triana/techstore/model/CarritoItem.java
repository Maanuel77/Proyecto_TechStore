package com.salesianos.triana.techstore.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// NO es entidad JPA: vive en la sesión HTTP del usuario mientras compra.
// Solo cuando tramita el pedido se convierte en LineaPedido y se persiste.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItem implements Serializable {

    // La garantía extendida añade un 10% del precio por unidad.
    private static final double COSTE_GARANTIA_PORCENTAJE = 0.10;

    private Long productoId;
    private String nombre;
    private Double precio;
    private Integer cantidad;
    private boolean garantiaExtendida = false;
    private Integer garantiaMeses;

    public CarritoItem(Long productoId, String nombre, Double precio, Integer cantidad, Integer garantiaMeses) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.garantiaMeses = garantiaMeses;
    }

    public Double getSubtotal() {
        return precio * cantidad;
    }

    // Coste de la garantía extendida: 10% del precio por unidad
    public Double getCosteGarantia() {
        return garantiaExtendida ? precio * cantidad * COSTE_GARANTIA_PORCENTAJE : 0.0;
    }
}
