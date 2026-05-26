package com.salesianos.triana.techstore.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.salesianos.triana.techstore.dto.ClienteGastoDto;
import com.salesianos.triana.techstore.dto.ClienteRankingDto;
import com.salesianos.triana.techstore.dto.DiaVentasDto;
import com.salesianos.triana.techstore.dto.MesVentasDto;
import com.salesianos.triana.techstore.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByFechaBetween(LocalDate inicio, LocalDate fin);

    // Listado admin con JOIN FETCH para evitar N+1 (líneas + producto + cliente).
    // distinct evita filas duplicadas cuando hay varias líneas por pedido.
    @Query("""
           select distinct p from Pedido p
           left join fetch p.lineas l
           left join fetch l.producto
           left join fetch p.cliente
           order by p.fecha desc
           """)
    List<Pedido> findAllWithLineasAndClienteOrderByFechaDesc();

    @Query("""
           select distinct p from Pedido p
           left join fetch p.lineas l
           left join fetch l.producto
           left join fetch p.cliente
           where p.fecha between :desde and :hasta
           order by p.fecha desc
           """)
    List<Pedido> findByFechaBetweenWithLineasAndClienteOrderByFechaDesc(LocalDate desde, LocalDate hasta);

    // Historial privado del cliente, identificado por email (puente User-Cliente).
    @Query("""
           select distinct p from Pedido p
           left join fetch p.lineas l
           left join fetch l.producto
           where p.cliente.email = :email
           order by p.fecha desc
           """)
    List<Pedido> findByClienteEmailWithLineasOrderByFechaDesc(String email);

    // Ranking de clientes por gasto total.
    @Query("""
           select new com.salesianos.triana.techstore.dto.ClienteGastoDto(
                 p.cliente, sum(p.total))
           from Pedido p
           where p.cliente is not null
           group by p.cliente
           order by sum(p.total) desc
           """)
    List<ClienteGastoDto> findClientesConMayorGasto();

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

    // Evolución diaria del rango: [fecha, nPedidos, ingresos].
    @Query("""
           select new com.salesianos.triana.techstore.dto.DiaVentasDto(
                 p.fecha, count(p), sum(p.total))
           from Pedido p
           where p.fecha between :desde and :hasta
           group by p.fecha
           order by p.fecha asc
           """)
    List<DiaVentasDto> findPedidosPorDia(LocalDate desde, LocalDate hasta);

    // Evolución mensual (cuando el rango es amplio): [año, mes, nPedidos, ingresos].
    // EXTRACT devuelve numérico; Hibernate lo mapea a Integer en el DTO.
    @Query("""
           select new com.salesianos.triana.techstore.dto.MesVentasDto(
                 cast(extract(year from p.fecha) as integer),
                 cast(extract(month from p.fecha) as integer),
                 count(p), sum(p.total))
           from Pedido p
           where p.fecha between :desde and :hasta
           group by extract(year from p.fecha), extract(month from p.fecha)
           order by extract(year from p.fecha), extract(month from p.fecha)
           """)
    List<MesVentasDto> findPedidosPorMes(LocalDate desde, LocalDate hasta);

    // Ranking de clientes: cliente + nº pedidos + gasto total + ticket medio.
    // Solo incluye clientes con al menos 1 pedido (group by implícito).
    // El sentido del orden (Top vs Bottom) se decide vía Pageable + Sort.
    @Query("""
           select new com.salesianos.triana.techstore.dto.ClienteRankingDto(
                 p.cliente, count(p), sum(p.total), avg(p.total))
           from Pedido p
           where p.cliente is not null
           group by p.cliente
           order by sum(p.total) desc
           """)
    List<ClienteRankingDto> findTopClientesByGasto(Pageable pageable);

    @Query("""
           select new com.salesianos.triana.techstore.dto.ClienteRankingDto(
                 p.cliente, count(p), sum(p.total), avg(p.total))
           from Pedido p
           where p.cliente is not null
           group by p.cliente
           order by sum(p.total) asc
           """)
    List<ClienteRankingDto> findBottomClientesByGasto(Pageable pageable);

    @Query("""
           select new com.salesianos.triana.techstore.dto.ClienteRankingDto(
                 p.cliente, count(p), sum(p.total), avg(p.total))
           from Pedido p
           where p.cliente is not null and p.fecha between :desde and :hasta
           group by p.cliente
           order by sum(p.total) desc
           """)
    List<ClienteRankingDto> findTopClientesByGastoBetween(LocalDate desde, LocalDate hasta, Pageable pageable);

    @Query("""
           select new com.salesianos.triana.techstore.dto.ClienteRankingDto(
                 p.cliente, count(p), sum(p.total), avg(p.total))
           from Pedido p
           where p.cliente is not null and p.fecha between :desde and :hasta
           group by p.cliente
           order by sum(p.total) asc
           """)
    List<ClienteRankingDto> findBottomClientesByGastoBetween(LocalDate desde, LocalDate hasta, Pageable pageable);

    // Gasto medio por cliente: total ingresos / nº de clientes activos.
    // coalesce protege ante null y nullif evita división por cero.
    @Query("""
           select coalesce(sum(p.total), 0) / nullif(count(distinct p.cliente), 0)
           from Pedido p
           where p.cliente is not null
           """)
    Double getGastoMedioPorCliente();

    @Query("""
           select coalesce(sum(p.total), 0) / nullif(count(distinct p.cliente), 0)
           from Pedido p
           where p.cliente is not null and p.fecha between :desde and :hasta
           """)
    Double getGastoMedioPorClienteBetween(LocalDate desde, LocalDate hasta);

    // Nº de clientes con al menos 1 pedido en un rango.
    @Query("""
           select count(distinct p.cliente)
           from Pedido p
           where p.cliente is not null and p.fecha between :desde and :hasta
           """)
    Long countClientesActivosBetween(LocalDate desde, LocalDate hasta);
}
