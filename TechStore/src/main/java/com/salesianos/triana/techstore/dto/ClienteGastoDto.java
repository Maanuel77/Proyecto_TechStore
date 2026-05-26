package com.salesianos.triana.techstore.dto;

import com.salesianos.triana.techstore.security.Cliente;

// Cliente + gasto acumulado. Ranking de clientes por importe.
public record ClienteGastoDto(Cliente cliente, Double gasto) { }
