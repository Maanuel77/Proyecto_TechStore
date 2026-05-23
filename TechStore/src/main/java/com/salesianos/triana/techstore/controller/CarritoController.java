package com.salesianos.triana.techstore.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.salesianos.triana.techstore.model.CarritoItem;
import com.salesianos.triana.techstore.model.Cliente;
import com.salesianos.triana.techstore.model.LineaPedido;
import com.salesianos.triana.techstore.model.Pedido;
import com.salesianos.triana.techstore.model.Producto;
import com.salesianos.triana.techstore.security.User;
import com.salesianos.triana.techstore.service.CarritoService;
import com.salesianos.triana.techstore.service.ClienteService;
import com.salesianos.triana.techstore.service.PedidoService;
import com.salesianos.triana.techstore.service.ProductoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;
    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final ClienteService clienteService;


    @GetMapping
    public String verCarrito(Model model) {
        model.addAttribute("items", carritoService.getItems());
        model.addAttribute("total", carritoService.calcularTotal());
        return "carrito/carrito";
    }


    @GetMapping("/anadir/{id}")
    public String anadirProducto(@PathVariable Long id) {
        // Si no existe, buscarPorId lanza NoSuchElementException
        // Si no hay stock, addProducto lanza SinStockException
        // Ambas las captura nuestro ExceptionControllerAdvice
        Producto p = productoService.buscarPorId(id);
        carritoService.addProducto(p);
        return "redirect:/catalogo";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        carritoService.removeProducto(id);
        return "redirect:/carrito";
    }

    @GetMapping("/garantia/{id}")
    public String toggleGarantia(@PathVariable Long id) {
        carritoService.toggleGarantia(id);
        return "redirect:/carrito";
    }

    @GetMapping("/tramitar")
    public String tramitar(@AuthenticationPrincipal User usuario, Model model) {
        if (carritoService.isEmpty()) {
            return "redirect:/carrito";
        }

        // Copiamos los datos ANTES de tocar nada para poder mostrarlos en la confirmación
        Map<Long, CarritoItem> itemsConfirmados = new LinkedHashMap<>(carritoService.getItems());
        double total = carritoService.calcularTotal();

        // 1. Buscar (o crear si es su primera compra) el Cliente del dominio
        //    asociado al usuario logueado.
        Cliente cliente = clienteService.findOrCreateForUser(usuario);

        // 2. Construir el Pedido con sus líneas a partir del carrito.
        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .build();

        for (CarritoItem item : itemsConfirmados.values()) {
            LineaPedido linea = LineaPedido.builder()
                    .producto(Producto.builder().id(item.getProductoId()).build())
                    .cantidad(item.getCantidad())
                    .garantiaExtendida(item.isGarantiaExtendida())
                    .costeGarantia(item.getCosteGarantia())
                    .build();
            pedido.addLineaPedido(linea);
        }

        // 3. Persistir el Pedido. Esta llamada:
        //    - reduce el stock de cada producto
        //    - lanza SinStockException si alguna línea pide más unidades que las disponibles
        //    - lanza NoSuchElementException si algún producto no existe
        //    Ambas son capturadas por nuestro ExceptionControllerAdvice.
        //    Como guardarPedido es @Transactional, si algo falla se hace rollback.
        pedidoService.guardarPedido(pedido);

        // 4. Solo si todo ha ido bien, vaciamos el carrito.
        carritoService.vaciarCarrito();

        model.addAttribute("items", itemsConfirmados);
        model.addAttribute("total", total);
        return "carrito/confirmacion";
    }

    @GetMapping("/vaciar")
    public String vaciar() {
        carritoService.vaciarCarrito();
        return "redirect:/catalogo";
    }
}
