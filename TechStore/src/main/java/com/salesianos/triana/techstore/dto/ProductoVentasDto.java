package com.salesianos.triana.techstore.dto;

import com.salesianos.triana.techstore.model.Producto;

// Producto + nº de unidades vendidas. Resultado de findMasVendidos.
public record ProductoVentasDto(Producto producto, Long cantidad) { }
