package com.salesianos.triana.techstore.service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.salesianos.triana.techstore.dto.ClienteGastoDto;
import com.salesianos.triana.techstore.dto.DiaVentasDto;
import com.salesianos.triana.techstore.dto.MesVentasDto;
import com.salesianos.triana.techstore.exceptions.SinStockException;
import com.salesianos.triana.techstore.model.Pedido;
import com.salesianos.triana.techstore.model.Producto;
import com.salesianos.triana.techstore.repository.PedidoRepository;
import com.salesianos.triana.techstore.repository.ProductoRepository;
import com.salesianos.triana.techstore.security.Usuario;
import com.salesianos.triana.techstore.service.base.BaseServiceImpl;

@Service
public class PedidoService extends BaseServiceImpl<Pedido, Long, PedidoRepository> {

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<Pedido> findByFechaBetween(LocalDate desde, LocalDate hasta) {
        return repository.findByFechaBetween(desde, hasta);
    }

    // Carga TODOS los pedidos con sus líneas, producto y cliente en una sola query
    // (JOIN FETCH) — evita el problema N+1 al pintar la tabla del admin.
    @Transactional(readOnly = true)
    public List<Pedido> findAllOrdered() {
        return repository.findAllWithLineasAndClienteOrderByFechaDesc();
    }

    @Transactional(readOnly = true)
    public List<Pedido> findByFechaBetweenOrdered(LocalDate desde, LocalDate hasta) {
        return repository.findByFechaBetweenWithLineasAndClienteOrderByFechaDesc(desde, hasta);
    }

    // Historial del cliente logueado.
    @Transactional(readOnly = true)
    public List<Pedido> findByUser(Usuario user) {
        return repository.findByClienteEmailWithLineasOrderByFechaDesc(user.getEmail());
    }

    @Transactional(readOnly = true)
    public List<ClienteGastoDto> findClientesConMayorGasto() {
        return repository.findClientesConMayorGasto();
    }

    // Las tres siguientes protegen contra null cuando aún no hay pedidos en BD.
    @Transactional(readOnly = true)
    public Double getTotalIngresos() {
        Double val = repository.getTotalIngresos();
        return val != null ? val : 0.0;
    }

    @Transactional(readOnly = true)
    public Double getTicketMedio() {
        Double val = repository.getTicketMedio();
        return val != null ? val : 0.0;
    }

    @Transactional(readOnly = true)
    public Long countClientesActivos() {
        Long val = repository.countClientesActivos();
        return val != null ? val : 0L;
    }

    // KPIs por rango de fechas (todos null-safe).
    @Transactional(readOnly = true)
    public long countPedidosBetween(LocalDate desde, LocalDate hasta) {
        return repository.countByFechaBetween(desde, hasta);
    }

    @Transactional(readOnly = true)
    public Double getTotalIngresosBetween(LocalDate desde, LocalDate hasta) {
        Double v = repository.getTotalIngresosBetween(desde, hasta);
        return v != null ? v : 0.0;
    }

    @Transactional(readOnly = true)
    public Double getTicketMedioBetween(LocalDate desde, LocalDate hasta) {
        Double v = repository.getTicketMedioBetween(desde, hasta);
        return v != null ? v : 0.0;
    }

    // Evolución temporal: día a día si el rango es corto, mes a mes si es largo.
    @Transactional(readOnly = true)
    public List<DiaVentasDto> findPedidosPorDia(LocalDate desde, LocalDate hasta) {
        return repository.findPedidosPorDia(desde, hasta);
    }

    @Transactional(readOnly = true)
    public List<MesVentasDto> findPedidosPorMes(LocalDate desde, LocalDate hasta) {
        return repository.findPedidosPorMes(desde, hasta);
    }

    // Persiste el pedido y descuenta stock. @Transactional para que cualquier
    // fallo (producto inexistente, sin stock...) revierta TODOS los cambios.
    @Transactional
    public Pedido guardarPedido(Pedido pedido) {
        pedido.setFecha(LocalDate.now());
        if (pedido.getCodigo() == null || pedido.getCodigo().isBlank()) {
            pedido.setCodigo("PED-" + System.currentTimeMillis());
        }

        pedido.getLineas().forEach(linea -> {
            Producto p = productoRepository.findById(linea.getProducto().getId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "No se ha encontrado el producto con id " + linea.getProducto().getId()));

            if (linea.getCantidad() > p.getStock()) {
                throw new SinStockException(
                    "No hay stock suficiente del producto '" + p.getNombre()
                    + "'. Solicitadas " + linea.getCantidad()
                    + " unidades, disponibles " + p.getStock() + ".");
            }

            // Descontamos stock y congelamos el precio del producto en la línea.
            p.setStock(p.getStock() - linea.getCantidad());
            productoRepository.save(p);
            linea.setPedido(pedido);
            linea.setPrecioUnitario(p.getPrecio());
            linea.setSubtotal(p.getPrecio() * linea.getCantidad());
        });

        double total = pedido.getLineas().stream()
                .mapToDouble(l -> l.getSubtotal() + (l.getCosteGarantia() != null ? l.getCosteGarantia() : 0.0))
                .sum();
        pedido.setTotal(total);
        return repository.save(pedido);
    }
}
