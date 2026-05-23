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

/**
 * Servicio de carrito almacenado en sesión HTTP.
 * Cada usuario tiene su propia instancia del carrito (igual que el ejemplo del profesor).
 */
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CarritoService {

    // clave = productoId, valor = CarritoItem con todos los datos del producto
    private final Map<Long, CarritoItem> items = new LinkedHashMap<>();

    /**
     * Añade un producto al carrito.
     * Si ya existe, incrementa la cantidad en 1.
     *
     * Lanza {@link SinStockException} si la cantidad pedida supera el stock
     * disponible. La excepción es capturada por el ControllerAdvice global.
     */
    public void addProducto(Producto p) {
        if (items.containsKey(p.getId())) {
            CarritoItem item = items.get(p.getId());

            // Uso de nuestra excepción personalizada de negocio
            if (item.getCantidad() + 1 > p.getStock()) {
                throw new SinStockException(
                    "No queda más stock disponible del producto '" + p.getNombre()
                    + "'. Solo hay " + p.getStock() + " unidades.");
            }

            item.setCantidad(item.getCantidad() + 1);
        } else {

            // Uso de nuestra excepción personalizada de negocio
            if (p.getStock() < 1) {
                throw new SinStockException(
                    "El producto '" + p.getNombre() + "' está agotado.");
            }

            items.put(p.getId(),
                new CarritoItem(p.getId(), p.getNombre(), p.getPrecio(), 1, p.getGarantiaMeses()));
        }
    }

    /**
     * Elimina completamente un producto del carrito por su id.
     */
    public void removeProducto(Long productoId) {
        items.remove(productoId);
    }

    /**
     * Devuelve una vista no modificable del carrito.
     */
    public Map<Long, CarritoItem> getItems() {
        return Collections.unmodifiableMap(items);
    }

    /**
     * Calcula el total del carrito sumando subtotal + coste de garantía de cada línea.
     */
    public double calcularTotal() {
        return items.values().stream()
                .mapToDouble(item -> item.getSubtotal() + item.getCosteGarantia())
                .sum();
    }

    /**
     * Devuelve el número total de unidades en el carrito (para el badge del navbar).
     */
    public int getCantidadTotal() {
        return items.values().stream()
                .mapToInt(CarritoItem::getCantidad)
                .sum();
    }

    /**
     * Activa/desactiva la garantía extendida de un item.
     */
    public void toggleGarantia(Long productoId) {
        CarritoItem item = items.get(productoId);
        if (item != null) {
            item.setGarantiaExtendida(!item.isGarantiaExtendida());
        }
    }

    /**
     * Vacía el carrito por completo.
     */
    public void vaciarCarrito() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
