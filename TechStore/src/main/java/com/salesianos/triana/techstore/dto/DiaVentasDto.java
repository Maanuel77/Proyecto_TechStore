package com.salesianos.triana.techstore.dto;

import java.time.LocalDate;

// Pedidos e ingresos agregados de un día concreto.
public record DiaVentasDto(LocalDate fecha, Long pedidos, Double ingresos) { }
