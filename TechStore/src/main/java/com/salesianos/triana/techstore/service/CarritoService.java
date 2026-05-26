package com.salesianos.triana.techstore.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.context.annotation.Scope;

import com.salesianos.triana.techstore.exceptions.SinStockException;
import com.salesianos.triana.techstore.model.CarritoItem;
import com.salesianos.triana.techstore.model.Producto;

// Carrito en sesión: cada usuario tiene su propia instancia mientras
// dura su sesión HTTP. Solo se persiste en BD al tramitar el pedido.
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CarritoService {

    // LinkedHashMap para conservar el orden en que se añadieron los productos.
    private final Map<Long, CarritoItem> items = new LinkedHashMap<>();

    // Añade 1 unidad. Si ya estaba en el carrito incrementa la cantidad.
    public void addProducto(Producto p) {
        addProducto(p, 1);
    }

    // Versión con cantidad explícita (la usa el modal del catálogo).
    // Valida cantidad mínima y stock acumulado en el carrito.
    public void addProducto(Producto p, int cantidad) {

        if (cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser al menos 1.");
        }

        if (items.containsKey(p.getId())) {
            CarritoItem item = items.get(p.getId());
            int nuevaCantidad = item.getCantidad() + cantidad;

            if (nuevaCantidad > p.getStock()) {
                throw new SinStockException(
                    "No queda stock suficiente del producto '" + p.getNombre()
                    + "'. Solo hay " + p.getStock() + " unidades disponibles.");
            }

            item.setCantidad(nuevaCantidad);
            // Refrescamos el stock máximo por si el catálogo cambió.
            item.setStockMaximo(p.getStock());
        } else {

            if (p.getStock() < 1) {
                throw new SinStockException(
                    "El producto '" + p.getNombre() + "' está agotado.");
            }
            if (cantidad > p.getStock()) {
                throw new SinStockException(
                    "No hay tantas unidades del producto '" + p.getNombre()
                    + "'. Solo hay " + p.getStock() + " disponibles.");
            }

            items.put(p.getId(), new CarritoItem(p, cantidad));
        }
    }

    public void removeProducto(Long productoId) {
        items.remove(productoId);
    }

    // Actualiza la cantidad de un item al valor indicado (validando contra
    // el stock real del producto). Si la cantidad llega a 0, lo elimina.
    public void actualizarCantidad(Producto p, int cantidad) {
        CarritoItem item = items.get(p.getId());
        if (item == null) return;

        if (cantidad < 1) {
            items.remove(p.getId());
            return;
        }
        if (cantidad > p.getStock()) {
            throw new SinStockException(
                "Solo quedan " + p.getStock() + " unidades de '" + p.getNombre() + "'.");
        }

        item.setCantidad(cantidad);
        item.setStockMaximo(p.getStock());
    }

    // Vista inmodificable para que las plantillas no manipulen el carrito.
    public Map<Long, CarritoItem> getItems() {
        return Collections.unmodifiableMap(items);
    }

    // Total = suma de subtotales + costes de garantía extendida.
    public double calcularTotal() {
        return items.values().stream()
                .mapToDouble(item -> item.getSubtotal() + item.getCosteGarantia())
                .sum();
    }

    // Total de unidades para el badge del navbar.
    public int getCantidadTotal() {
        return items.values().stream()
                .mapToInt(CarritoItem::getCantidad)
                .sum();
    }

    public void toggleGarantia(Long productoId) {
        CarritoItem item = items.get(productoId);
        if (item != null) {
            item.setGarantiaExtendida(!item.isGarantiaExtendida());
        }
    }

    public void vaciarCarrito() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
