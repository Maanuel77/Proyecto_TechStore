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
    private String marca;
    private String imagenUrl;
    private Double precio;
    private Integer cantidad;
    private boolean garantiaExtendida = false;
    private Integer garantiaMeses;
    private boolean refurbished = false;
    // Snapshot del stock cuando se añadió: el frontend lo usa como tope del
    // stepper. El backend lo revalida contra el stock REAL al actualizar.
    private Integer stockMaximo;

    public CarritoItem(Producto p, Integer cantidad) {
        this.productoId = p.getId();
        this.nombre = p.getNombre();
        this.marca = p.getMarca();
        this.imagenUrl = p.getImagenUrl();
        this.precio = p.getPrecio();
        this.cantidad = cantidad;
        this.garantiaMeses = p.getGarantiaMeses();
        this.refurbished = Boolean.TRUE.equals(p.getRefurbished());
        this.stockMaximo = p.getStock();
    }

    public Double getSubtotal() {
        return precio * cantidad;
    }

    // Coste de la garantía extendida: 10% del precio por unidad
    public Double getCosteGarantia() {
        return garantiaExtendida ? precio * cantidad * COSTE_GARANTIA_PORCENTAJE : 0.0;
    }
}
