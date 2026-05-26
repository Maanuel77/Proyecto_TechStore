package com.salesianos.triana.techstore.dto;

import com.salesianos.triana.techstore.model.Producto;

// Producto + unidades vendidas + ingresos generados. Top de ventas.
public record ProductoTopDto(Producto producto, Long cantidad, Double ingresos) { }
