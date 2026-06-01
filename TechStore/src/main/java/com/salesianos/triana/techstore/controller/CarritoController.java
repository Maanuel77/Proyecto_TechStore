package com.salesianos.triana.techstore.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.salesianos.triana.techstore.cupon.Cupon;
import com.salesianos.triana.techstore.cupon.CuponService;
import com.salesianos.triana.techstore.dto.ProductoTopDto;
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

    // Umbral de envío gratuito (mostrado en la barra de progreso del sidebar).
    private static final double ENVIO_GRATIS_DESDE = 1500.0;
    // Nº de productos sugeridos a mostrar bajo el carrito.
    private static final int N_SUGERIDOS = 4;

    private final CarritoService carritoService;
    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final CuponService cuponService;


    @GetMapping
    public String verCarrito(@AuthenticationPrincipal Cliente cliente, Model model) {
        Map<Long, CarritoItem> items = carritoService.getItems();
        model.addAttribute("items", items);
        model.addAttribute("subtotal", carritoService.calcularSubtotal());
        model.addAttribute("descuento", carritoService.calcularDescuento());
        model.addAttribute("total", carritoService.calcularTotal());
        model.addAttribute("envioGratisDesde", ENVIO_GRATIS_DESDE);
        model.addAttribute("cuponAplicado", carritoService.getCuponAplicado());
        model.addAttribute("porcentajeDescuento", carritoService.getPorcentajeDescuento());
        // Si el cliente tiene cupón de fidelidad asignado, lo mostramos como
        // sugerencia en el sidebar (encima del input).
        Optional<Cupon> fidelidad = cuponService.getCuponFidelidad(cliente);
        model.addAttribute("cuponFidelidad", fidelidad.orElse(null));
        // Top productos para el bloque "Te puede interesar", excluyendo los
        // que ya están en el carrito.
        List<Producto> sugeridos = productoService.findTopVendidos(N_SUGERIDOS + items.size()).stream()
                .map(ProductoTopDto::producto)
                .filter(p -> !items.containsKey(p.getId()))
                .limit(N_SUGERIDOS)
                .toList();
        model.addAttribute("sugeridos", sugeridos);
        return "carrito/carrito";
    }

    // Endpoint genérico de añadir. El parámetro opcional `volver` decide
    // a dónde se redirige: "carrito" (cuando se añade desde el carrito) o
    // "catalogo" (por defecto, cuando se añade desde el catálogo).
    @GetMapping("/anadir/{id}")
    public String anadirProducto(@PathVariable Long id,
                                 @RequestParam(defaultValue = "1") Integer cantidad,
                                 @RequestParam(defaultValue = "catalogo") String volver) {
        Producto p = productoService.buscarPorId(id);
        carritoService.addProducto(p, cantidad);
        return "carrito".equals(volver) ? "redirect:/carrito" : "redirect:/catalogo";
    }

    // Stepper +1 desde el carrito.
    @GetMapping("/incrementar/{id}")
    public String incrementar(@PathVariable Long id) {
        Producto p = productoService.buscarPorId(id);
        carritoService.addProducto(p, 1);
        return "redirect:/carrito";
    }

    // Stepper -1 desde el carrito (si llega a 0 elimina el item).
    @GetMapping("/decrementar/{id}")
    public String decrementar(@PathVariable Long id) {
        Producto p = productoService.buscarPorId(id);
        CarritoItem item = carritoService.getItems().get(id);
        int nueva = (item != null) ? item.getCantidad() - 1 : 0;
        carritoService.actualizarCantidad(p, nueva);
        return "redirect:/carrito";
    }

    // Input numérico: el usuario teclea la cantidad y envía el form.
    @GetMapping("/cantidad/{id}")
    public String setCantidad(@PathVariable Long id, @RequestParam int cantidad) {
        Producto p = productoService.buscarPorId(id);
        carritoService.actualizarCantidad(p, cantidad);
        return "redirect:/carrito";
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

    // Cupón

    @GetMapping("/cupon/aplicar")
    public String aplicarCupon(@AuthenticationPrincipal Cliente cliente,
                               @RequestParam String codigo,
                               RedirectAttributes ra) {
        Optional<Cupon> cupon = cuponService.validar(codigo, cliente);
        if (cupon.isPresent()) {
            Cupon c = cupon.get();
            carritoService.aplicarCupon(c.getCodigo(), c.getDescuento());
            int pct = (int) Math.round(c.getDescuento() * 100);
            ra.addFlashAttribute("exitoMensaje",
                    "¡Cupón aplicado! Descuento del " + pct + "% activado.");
        } else {
            ra.addFlashAttribute("errorCarrito",
                    "El código \"" + codigo + "\" no es válido o ha caducado.");
        }
        return "redirect:/carrito";
    }

    @GetMapping("/cupon/eliminar")
    public String eliminarCupon(RedirectAttributes ra) {
        carritoService.eliminarCupon();
        ra.addFlashAttribute("exitoMensaje", "Cupón retirado del pedido.");
        return "redirect:/carrito";
    }

    // Tramitación

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
        String cuponCodigo = carritoService.getCuponAplicado();      // null si no hay cupón
        double cuponPct    = carritoService.getPorcentajeDescuento(); // 0 si no hay cupón

        // Construimos el Pedido con el snapshot del cupón aplicado (si lo hay).
        // El descuento se calcula dentro de guardarPedido a partir de cuponDescuento.
        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .cuponCodigo(cuponCodigo)
                .cuponDescuento(cuponCodigo != null ? cuponPct : null)
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

        // Persistir: reduce stock, calcula subtotal por líneas y total con descuento.
        pedidoService.guardarPedido(pedido);

        // Consumir el cupón usado. Si era FIDELIDAD se desactiva (single-use);
        // si era PUBLICO se queda como estaba (sigue valiendo para otros clientes).
        if (cuponCodigo != null) {
            cuponService.consumir(cuponCodigo);
        }

        // Tras la compra, ver si el cliente acaba de cruzar el siguiente
        // umbral acumulativo de fidelidad y asignarle un cupón nuevo.
        cuponService.asignarFidelidadSiProcede(cliente);

        // Vaciamos el carrito (limpia también el cupón aplicado en sesión).
        carritoService.vaciarCarrito();

        // Subtotal = suma de las líneas + garantías (lo que se cobraría SIN cupón).
        // Descuento = diferencia entre subtotal y total final, que ya viene aplicado.
        double subtotal = itemsConfirmados.values().stream()
                .mapToDouble(i -> i.getSubtotal()
                        + (i.getCosteGarantia() != null ? i.getCosteGarantia() : 0.0))
                .sum();
        double descuento = subtotal - pedido.getTotal();

        model.addAttribute("items", itemsConfirmados);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("descuento", descuento);
        model.addAttribute("cuponCodigo", cuponCodigo);
        model.addAttribute("total", pedido.getTotal());
        return "carrito/confirmacion";
    }

    @GetMapping("/vaciar")
    public String vaciar() {
        carritoService.vaciarCarrito();
        return "redirect:/catalogo";
    }
}
