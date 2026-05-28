package com.salesianos.triana.techstore.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.salesianos.triana.techstore.dto.MarcaVentasDto;
import com.salesianos.triana.techstore.dto.ProductoStockDto;
import com.salesianos.triana.techstore.dto.ProductoTopDto;
import com.salesianos.triana.techstore.dto.ProductoVentasDto;
import com.salesianos.triana.techstore.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Productos con stock por debajo del valor dado (alertas de stock).
    List<Producto> findByStockLessThanEqual(Integer stock);

    // Listado del catálogo ordenado por stock ascendente (los más bajos arriba).
    @Query("select p from Producto p order by p.stock asc")
    List<Producto> findByLowAvailability();

    // Devuelve [Producto, unidades vendidas] para el ranking simple de ventas.
    @Query("""
           select new com.salesianos.triana.techstore.dto.ProductoVentasDto(
                 lp.producto, sum(lp.cantidad))
           from LineaPedido lp
           group by lp.producto
           order by sum(lp.cantidad) desc
           """)
    List<ProductoVentasDto> findMasVendidos();

    // Ventas agregadas por marca.
    @Query("""
           select new com.salesianos.triana.techstore.dto.MarcaVentasDto(
                 lp.producto.marca, sum(lp.cantidad))
           from LineaPedido lp
           group by lp.producto.marca
           order by sum(lp.cantidad) desc
           """)
    List<MarcaVentasDto> findVentasPorMarca();

    // Top de productos: producto + unidades + ingresos. Limitado por Pageable.
    @Query("""
           select new com.salesianos.triana.techstore.dto.ProductoTopDto(
                 lp.producto, sum(lp.cantidad), sum(lp.subtotal))
           from LineaPedido lp
           group by lp.producto
           order by sum(lp.cantidad) desc
           """)
    List<ProductoTopDto> findTopVendidos(Pageable pageable);

    // Igual que el anterior pero filtrando por rango de fechas del pedido.
    @Query("""
           select new com.salesianos.triana.techstore.dto.ProductoTopDto(
                 lp.producto, sum(lp.cantidad), sum(lp.subtotal))
           from LineaPedido lp
           where lp.pedido.fecha between :desde and :hasta
           group by lp.producto
           order by sum(lp.cantidad) desc
           """)
    List<ProductoTopDto> findTopVendidosBetween(LocalDate desde, LocalDate hasta, Pageable pageable);

    // Productos con stock <= umbral, junto con sus unidades vendidas desde la
    // fecha indicada. La subquery devuelve 0 si el producto no se ha vendido
    // en el periodo (coalesce). Ordenado por stock ascendente → arriba los
    // más urgentes.
    @Query("""
           select new com.salesianos.triana.techstore.dto.ProductoStockDto(
                 p,
                 (select coalesce(sum(lp.cantidad), 0L) from LineaPedido lp
                  where lp.producto = p and lp.pedido.fecha >= :desde))
           from Producto p
           where p.stock <= :umbral
           order by p.stock asc
           """)
    List<ProductoStockDto> findStockBajoConVentas(int umbral, LocalDate desde);
}
