package com.salesianos.triana.techstore.dto;

// Pedidos e ingresos agregados de un mes concreto. EXTRACT en JPQL devuelve
// Integer (algunos drivers Long), por eso los campos numéricos son Number.
public record MesVentasDto(Integer anio, Integer mes, Long pedidos, Double ingresos) {

    // Etiqueta legible "MM/yyyy" para usar en tablas y gráficos.
    public String etiqueta() {
        return String.format("%02d/%d", mes, anio);
    }
}
