package com.salesianos.triana.techstore.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.salesianos.triana.techstore.model.CarritoItem;
import com.salesianos.triana.techstore.service.CarritoService;
import com.salesianos.triana.techstore.service.ProductoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;
    private final ProductoService productoService;


    @GetMapping
    public String verCarrito(Model model) {
        model.addAttribute("items", carritoService.getItems());
        model.addAttribute("total", carritoService.calcularTotal());
        return "carrito/carrito";
    }


    @GetMapping("/anadir/{id}")
    public String anadirProducto(@PathVariable Long id, RedirectAttributes ra) {
        productoService.findById(id).ifPresentOrElse(
            carritoService::addProducto,
            () -> ra.addFlashAttribute("errorCarrito", "Producto no encontrado")
        );
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
    public String tramitar(Model model) {
        if (carritoService.isEmpty()) {
            return "redirect:/carrito";
        }

        // Copiamos los datos ANTES de vaciar el carrito para mostrarlos en la confirmación
        Map<Long, CarritoItem> itemsConfirmados = new LinkedHashMap<>(carritoService.getItems());
        double total = carritoService.calcularTotal();

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
