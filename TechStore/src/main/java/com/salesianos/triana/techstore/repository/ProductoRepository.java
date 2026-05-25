package com.salesianos.triana.techstore.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.salesianos.triana.techstore.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Productos con stock por debajo del valor dado (alertas de stock).
    List<Producto> findByStockLessThanEqual(Integer stock);

    // Listado del catálogo ordenado por stock ascendente (los más bajos arriba).
    @Query("select p from Producto p order by p.stock asc")
    List<Producto> findByLowAvailability();

    // Devuelve pares [Producto, totalUnidadesVendidas] para el ranking de ventas.
    @Query("select lp.producto, sum(lp.cantidad) from LineaPedido lp group by lp.producto order by sum(lp.cantidad) desc")
    List<Object[]> findMasVendidos();

    // Devuelve pares [marca, totalUnidades] agrupando por marca.
    @Query("select lp.producto.marca, sum(lp.cantidad) from LineaPedido lp group by lp.producto.marca order by sum(lp.cantidad) desc")
    List<Object[]> findVentasPorMarca();

    // Ranking de productos: [Producto, unidades vendidas, ingresos generados].
    // Pageable se usa para limitar el top (p.ej. PageRequest.of(0, 10)).
    @Query("select lp.producto, sum(lp.cantidad), sum(lp.subtotal) "
         + "from LineaPedido lp "
         + "group by lp.producto "
         + "order by sum(lp.cantidad) desc")
    
    //Por qué no se le puede poner limit?
    List<Object[]> findTopVendidos(Pageable pageable);
    

    // Igual que findTopVendidos pero filtrando por rango de fechas del pedido.
    @Query("select lp.producto, sum(lp.cantidad), sum(lp.subtotal) "
         + "from LineaPedido lp "
         + "where lp.pedido.fecha between :desde and :hasta "
         + "group by lp.producto "
         + "order by sum(lp.cantidad) desc")
    List<Object[]> findTopVendidosBetween(LocalDate desde, LocalDate hasta, Pageable pageable);
}
