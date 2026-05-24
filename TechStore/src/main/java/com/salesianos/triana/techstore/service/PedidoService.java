package com.salesianos.triana.techstore.service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.salesianos.triana.techstore.exceptions.SinStockException;
import com.salesianos.triana.techstore.model.Pedido;
import com.salesianos.triana.techstore.model.Producto;
import com.salesianos.triana.techstore.repository.PedidoRepository;
import com.salesianos.triana.techstore.repository.ProductoRepository;
import com.salesianos.triana.techstore.security.User;
import com.salesianos.triana.techstore.service.base.BaseServiceImpl;

@Service
public class PedidoService extends BaseServiceImpl<Pedido, Long, PedidoRepository> {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Pedido> findByFechaBetween(LocalDate desde, LocalDate hasta) {
        return repository.findByFechaBetween(desde, hasta);
    }

    /** Todos los pedidos ordenados por fecha descendente. */
    public List<Pedido> findAllOrdered() {
        return repository.findAllByOrderByFechaDesc();
    }

    /** Pedidos en un rango de fechas, ordenados por fecha descendente. */
    public List<Pedido> findByFechaBetweenOrdered(LocalDate desde, LocalDate hasta) {
        return repository.findByFechaBetweenOrderByFechaDesc(desde, hasta);
    }

    /**
     * Devuelve el historial de pedidos del usuario logueado.
     * Une User (seguridad) y Cliente (dominio) por el email, igual que
     * hace ClienteService.findOrCreateForUser al crear pedidos.
     */
    public List<Pedido> findByUser(User user) {
        return repository.findByClienteEmailOrderByFechaDesc(user.getEmail());
    }

    public List<Object[]> findClientesConMayorGasto() {
        return repository.findClientesConMayorGasto();
    }

    public Double getTotalIngresos() {
        Double val = repository.getTotalIngresos();
        return val != null ? val : 0.0;
    }

    public Double getTicketMedio() {
        Double val = repository.getTicketMedio();
        return val != null ? val : 0.0;
    }

    public Long countClientesActivos() {
        Long val = repository.countClientesActivos();
        return val != null ? val : 0L;
    }

    @Transactional
    public Pedido guardarPedido(Pedido pedido) {
        pedido.setFecha(LocalDate.now());
        if (pedido.getCodigo() == null || pedido.getCodigo().isBlank()) {
            pedido.setCodigo("PED-" + System.currentTimeMillis());
        }

        pedido.getLineas().forEach(linea -> {
            // Si el producto no existe, lanzamos NoSuchElementException
            Producto p = productoRepository.findById(linea.getProducto().getId())
                    .orElseThrow(() -> new NoSuchElementException());

            // Si no hay stock suficiente, lanzamos nuestra excepción personalizada
            if (linea.getCantidad() > p.getStock()) {
                throw new SinStockException(
                    "No hay stock suficiente del producto '" + p.getNombre()
                    + "'. Solicitadas " + linea.getCantidad()
                    + " unidades, disponibles " + p.getStock() + ".");
            }

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
