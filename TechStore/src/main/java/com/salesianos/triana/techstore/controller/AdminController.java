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

import com.salesianos.triana.techstore.dto.ProductoStockDto;
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
    public String dashboard(@RequestParam(defaultValue = "5") int umbralStock,
                            Model model) {
        // KPI "productos en venta": solo cuenta activos (no incluye archivados).
        model.addAttribute("productos", productoService.countActivos());
        model.addAttribute("pedidos",   pedidoService.count());
        model.addAttribute("clientes",  userService.count());

        // Alertas de stock con contexto de ventas (últimos 30 días).
        List<ProductoStockDto> stockBajo = productoService.stockBajoConVentas(umbralStock);
        model.addAttribute("stockBajo", stockBajo);
        model.addAttribute("umbralStock", umbralStock);
        // Conteos por gravedad para los KPI cards de la sección.
        long nAgotados = stockBajo.stream().filter(s -> "AGOTADO".equals(s.getGravedad())).count();
        long nCriticos = stockBajo.stream().filter(s -> "CRITICO".equals(s.getGravedad())).count();
        long nBajos    = stockBajo.stream().filter(s -> "BAJO".equals(s.getGravedad())).count();
        model.addAttribute("nAgotados", nAgotados);
        model.addAttribute("nCriticos", nCriticos);
        model.addAttribute("nBajos",    nBajos);
        return "admin/dashboard";
    }

    @GetMapping("/productos")
    public String listadoProductos(Model model) {
        // Solo activos: los archivados viven en /admin/productos/archivados.
        model.addAttribute("productos", productoService.findActivos());
        return "admin/productos/list";
    }

    // Pestaña "Archivados": productos que han sido archivados (soft delete).
    // No aparecen en el catálogo público ni en el listado normal de productos,
    // pero el admin puede consultarlos y restaurarlos desde aquí.
    @GetMapping("/productos/archivados")
    public String listadoArchivados(Model model) {
        model.addAttribute("productos", productoService.findArchivados());
        return "admin/productos/archivados";
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

    // Archivar producto (soft delete). El producto deja de aparecer en el
    // catálogo público y en el listado de "Productos" del admin, pero sigue
    // existiendo en la BD para preservar la integridad de los pedidos antiguos
    // que lo referencian. Es reversible vía /producto/restaurar/{id}.
    @GetMapping("/producto/archivar/{id}")
    public String archivarProducto(@PathVariable Long id, RedirectAttributes ra) {
        productoService.archivar(id);
        ra.addFlashAttribute("infoProducto",
                "Producto archivado. Puedes restaurarlo desde la pestaña 'Archivados'.");
        return "redirect:/admin/productos";
    }

    // Restaurar producto: vuelve al catálogo público y al listado normal.
    @GetMapping("/producto/restaurar/{id}")
    public String restaurarProducto(@PathVariable Long id, RedirectAttributes ra) {
        productoService.restaurar(id);
        ra.addFlashAttribute("infoProducto", "Producto restaurado y de nuevo a la venta.");
        return "redirect:/admin/productos/archivados";
    }

    @GetMapping("/clientes")
    public String listadoClientes(Model model) {
        model.addAttribute("usuarios", userService.findAll());
        return "admin/clientes/list";
    }

    // Cambiar rol (ADMIN <-> CLIENTE). Las restricciones de negocio se validan
    // en el service y vuelven aquí como IllegalStateException → flash visible.
    // Cualquier otro error inesperado lo capturamos también para evitar el
    // whitelabel y dar feedback al admin (en lugar de un 500 mudo).
    @GetMapping("/clientes/toggle-role/{id}")
    public String toggleRole(@PathVariable Long id,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.toggleRole(id, principal.getName());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorRol", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorRol",
                    "No se ha podido cambiar el rol: " + e.getMessage());
        }
        return "redirect:/admin/clientes";
    }
}
