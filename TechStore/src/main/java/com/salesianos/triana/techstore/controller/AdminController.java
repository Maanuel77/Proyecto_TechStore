package com.salesianos.triana.techstore.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.salesianos.triana.techstore.model.Pedido;
import com.salesianos.triana.techstore.model.Producto;
import com.salesianos.triana.techstore.service.PedidoService;
import com.salesianos.triana.techstore.service.ProductoService;
import com.salesianos.triana.techstore.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// Panel de administración: dashboard, CRUD de productos, pedidos y usuarios.
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // count() emite SELECT COUNT(*), no trae las filas a memoria.
        model.addAttribute("productos", productoService.count());
        model.addAttribute("pedidos",   pedidoService.count());
        model.addAttribute("clientes",  userService.count());
        model.addAttribute("bajoStock", productoService.lowStock());
        return "admin/dashboard";
    }

    @GetMapping("/productos")
    public String listadoProductos(Model model) {
        model.addAttribute("productos", productoService.findAll());
        return "admin/productos/list";
    }

    // Listado de pedidos con filtro opcional por rango de fechas.
    @GetMapping("/pedidos")
    public String listadoPedidos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {

        List<Pedido> pedidos;
        if (desde != null && hasta != null) {
            pedidos = pedidoService.findByFechaBetweenOrdered(desde, hasta);
        } else {
            pedidos = pedidoService.findAllOrdered();
        }

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        // Stats globales (no se filtran: muestran el histórico completo).
        model.addAttribute("totalPedidos", pedidoService.count());
        model.addAttribute("ingresos", pedidoService.getTotalIngresos());
        model.addAttribute("ticketMedio", pedidoService.getTicketMedio());
        return "admin/pedidos/list";
    }

    @GetMapping("/producto/nuevo")
    public String nuevoProductoForm(Model model) {
        model.addAttribute("producto", new Producto());
        return "admin/producto/form";
    }

    @PostMapping("/producto/nuevo")
    public String nuevoProductoGuardar(@Valid @ModelAttribute("producto") Producto producto,
                                       BindingResult bindingResult) {
        // Si fallan las validaciones (@NotBlank, @Min...) volvemos al form.
        if (bindingResult.hasErrors()) {
            return "admin/producto/form";
        }
        // El service valida el precio máximo y lanza IllegalArgumentException
        // si se supera (la captura el ControllerAdvice y aparece como flash).
        productoService.save(producto);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/producto/editar/{id}")
    public String editarProductoForm(@PathVariable Long id, Model model) {
        Producto producto = productoService.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "No se ha encontrado el producto con id " + id));
        model.addAttribute("producto", producto);
        return "admin/producto/form";
    }

    @PostMapping("/producto/editar/{id}")
    public String editarProductoGuardar(@PathVariable Long id, @ModelAttribute Producto producto) {
        producto.setId(id);
        productoService.edit(producto);
        return "redirect:/admin/dashboard";
    }

    // Eliminar producto. El service lanza IllegalArgumentException si está
    // referenciado en pedidos antiguos (FK), gestionado por el ControllerAdvice.
    @GetMapping("/producto/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productoService.eliminar(id);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/clientes")
    public String listadoClientes(Model model) {
        model.addAttribute("usuarios", userService.findAll());
        return "admin/clientes/list";
    }

    // Cambiar rol (ADMIN <-> CLIENTE). Las restricciones (superadmin, propio
    // usuario) se validan en el service; aquí capturamos el error y lo pasamos
    // como flash a la vista del listado.
    @GetMapping("/clientes/toggle-role/{id}")
    public String toggleRole(@PathVariable Long id,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.toggleRole(id, principal.getName());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorRol", e.getMessage());
        }
        return "redirect:/admin/clientes";
    }
}
