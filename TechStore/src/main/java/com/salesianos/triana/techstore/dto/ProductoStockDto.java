package com.salesianos.triana.techstore.dto;

import com.salesianos.triana.techstore.model.Producto;

// Producto + unidades vendidas en los últimos N días. Lo usa la sección de
// "Alertas de stock" del dashboard para mostrar contexto (un stock bajo es
// urgente si además se está vendiendo mucho).
public record ProductoStockDto(Producto producto, Long vendidosRecientes) {

    // Clasificación de gravedad según el stock disponible.
    // Las plantillas la usan para pintar el badge de color.
    public String getGravedad() {
        Integer s = producto.getStock();
        if (s == null || s == 0) return "AGOTADO";
        if (s <= 2) return "CRITICO";
        if (s <= 5) return "BAJO";
        return "OK";
    }
}
