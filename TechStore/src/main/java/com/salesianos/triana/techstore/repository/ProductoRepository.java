package com.salesianos.triana.techstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.salesianos.triana.techstore.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByStockLessThanEqual(Integer stock);

    @Query("select p from Producto p order by p.stock asc")
    List<Producto> findByLowAvailability();

    @Query("select lp.producto, sum(lp.cantidad) from LineaPedido lp group by lp.producto order by sum(lp.cantidad) desc")
    List<Object[]> findMasVendidos();

    @Query("select lp.producto.marca, sum(lp.cantidad) from LineaPedido lp group by lp.producto.marca order by sum(lp.cantidad) desc")
    List<Object[]> findVentasPorMarca();
}

