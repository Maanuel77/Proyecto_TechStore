package com.salesianos.triana.techstore.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.salesianos.triana.techstore.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByFechaBetween(LocalDate inicio, LocalDate fin);

    // Listados ordenados por fecha desc (más reciente primero) para el admin.
    List<Pedido> findAllByOrderByFechaDesc();
    List<Pedido> findByFechaBetweenOrderByFechaDesc(LocalDate inicio, LocalDate fin);

    // Historial privado del cliente, identificado por email (puente User-Cliente).
    List<Pedido> findByClienteEmailOrderByFechaDesc(String email);

    // Ranking de clientes por gasto total. Devuelve pares [Cliente, sumaTotal].
    @Query("select p.cliente, sum(p.total) from Pedido p where p.cliente is not null group by p.cliente order by sum(p.total) desc")
    List<Object[]> findClientesConMayorGasto();

    // Estadísticas globales para el dashboard.
    @Query("select sum(p.total) from Pedido p")
    Double getTotalIngresos();

    @Query("select avg(p.total) from Pedido p")
    Double getTicketMedio();

    @Query("select count(distinct p.cliente) from Pedido p where p.cliente is not null")
    Long countClientesActivos();

    // KPIs del rango de fechas (todos protegidos con coalesce contra null).
    @Query("select coalesce(sum(p.total), 0) from Pedido p where p.fecha between :desde and :hasta")
    Double getTotalIngresosBetween(LocalDate desde, LocalDate hasta);

    @Query("select coalesce(avg(p.total), 0) from Pedido p where p.fecha between :desde and :hasta")
    Double getTicketMedioBetween(LocalDate desde, LocalDate hasta);

    long countByFechaBetween(LocalDate desde, LocalDate hasta);

    // Agregaciones para el gráfico de evolución temporal.
    // Diaria: [fecha, nPedidos, ingresos] por cada día con ventas en el rango.
    @Query("select p.fecha, count(p), sum(p.total) "
         + "from Pedido p "
         + "where p.fecha between :desde and :hasta "
         + "group by p.fecha "
         + "order by p.fecha asc")
    List<Object[]> findPedidosPorDia(LocalDate desde, LocalDate hasta);

    // Mensual: [año, mes, nPedidos, ingresos]. Se usa cuando el rango es amplio.
    @Query("select extract(year from p.fecha), extract(month from p.fecha), count(p), sum(p.total) "
         + "from Pedido p "
         + "where p.fecha between :desde and :hasta "
         + "group by extract(year from p.fecha), extract(month from p.fecha) "
         + "order by extract(year from p.fecha), extract(month from p.fecha)")
    List<Object[]> findPedidosPorMes(LocalDate desde, LocalDate hasta);
}
