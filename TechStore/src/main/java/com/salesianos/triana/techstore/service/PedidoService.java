package com.salesianos.triana.techstore.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.salesianos.triana.techstore.model.Pedido;
import com.salesianos.triana.techstore.model.Producto;
import com.salesianos.triana.techstore.repository.PedidoRepository;
import com.salesianos.triana.techstore.repository.ProductoRepository;
import com.salesianos.triana.techstore.service.base.BaseServiceImpl;

@Service
public class PedidoService extends BaseServiceImpl<Pedido, Long, PedidoRepository> {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Pedido> findByFechaBetween(LocalDate desde, LocalDate hasta) {
        return repository.findByFechaBetween(desde, hasta);
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
            Producto p = productoRepository.findById(linea.getProducto().getId()).orElseThrow();
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
