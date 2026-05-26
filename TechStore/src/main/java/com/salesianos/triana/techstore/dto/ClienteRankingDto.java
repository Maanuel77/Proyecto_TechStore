package com.salesianos.triana.techstore.dto;

import com.salesianos.triana.techstore.security.Cliente;

// Ranking de clientes por gasto: cliente + nº de pedidos + gasto total + ticket medio.
// Se usa tanto para el Top (orden DESC) como para el Bottom (orden ASC).
public record ClienteRankingDto(
        Cliente cliente,
        Long nPedidos,
        Double gastoTotal,
        Double ticketMedio
) { }
