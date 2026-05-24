package com.salesianos.triana.techstore.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.salesianos.triana.techstore.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByFechaBetween(LocalDate inicio, LocalDate fin);

    /**
     * Devuelve todos los pedidos de un cliente identificado por email,
     * ordenados por fecha descendente (más reciente primero).
     */
    List<Pedido> findByClienteEmailOrderByFechaDesc(String email);

    @Query("select p.cliente, sum(p.total) from Pedido p where p.cliente is not null group by p.cliente order by sum(p.total) desc")
    List<Object[]> findClientesConMayorGasto();

    @Query("select sum(p.total) from Pedido p")
    Double getTotalIngresos();

    @Query("select avg(p.total) from Pedido p")
    Double getTicketMedio();

    @Query("select count(distinct p.cliente) from Pedido p where p.cliente is not null")
    Long countClientesActivos();
}
