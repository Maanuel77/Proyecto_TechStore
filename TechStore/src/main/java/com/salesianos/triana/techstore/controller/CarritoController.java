package com.salesianos.triana.techstore.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.salesianos.triana.techstore.model.CarritoItem;
import com.salesianos.triana.techstore.model.LineaPedido;
import com.salesianos.triana.techstore.model.Pedido;
import com.salesianos.triana.techstore.model.Producto;
import com.salesianos.triana.techstore.security.Cliente;
import com.salesianos.triana.techstore.service.CarritoService;
import com.salesianos.triana.techstore.service.PedidoService;
import com.salesianos.triana.techstore.service.ProductoService;

import lombok.RequiredArgsConstructor;

// Operaciones del carrito (en sesión) y tramitación del pedido. Solo CLIENTE.
@Controller
@RequestMapping("/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;
    private final ProductoService productoService;
    private final PedidoService pedidoService;


    @GetMapping
    public String verCarrito(Model model) {
        model.addAttribute("items", carritoService.getItems());
        model.addAttribute("total", carritoService.calcularTotal());
        return "carrito/carrito";
    }

    // Usado por el botón "+" del catálogo (cantidad=1 por defecto) y por el
    // modal de detalle (que envía la cantidad como query param).
    @GetMapping("/anadir/{id}")
    public String anadirProducto(@PathVariable Long id,
                                 @RequestParam(defaultValue = "1") Integer cantidad) {
        Producto p = productoService.buscarPorId(id);
        carritoService.addProducto(p, cantidad);
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

    // Convierte el carrito en sesión en un Pedido persistido en BD.
    // El @AuthenticationPrincipal Cliente lo entrega Spring directamente
    // gracias a la herencia: solo los CLIENTE pueden entrar aquí (SecurityConfig).
    @GetMapping("/tramitar")
    public String tramitar(@AuthenticationPrincipal Cliente cliente, Model model) {
        if (carritoService.isEmpty()) {
            return "redirect:/carrito";
        }

        // Copia previa: si el guardado falla, el carrito sigue intacto;
        // si va bien, mostramos esta copia en la página de confirmación.
        Map<Long, CarritoItem> itemsConfirmados = new LinkedHashMap<>(carritoService.getItems());
        double total = carritoService.calcularTotal();

        // Construimos el Pedido y sus líneas a partir del carrito.
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

        pedidoService.guardarPedido(pedido);

        // Solo si todo ha ido bien vaciamos el carrito.
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
