package com.salesianos.triana.techstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.salesianos.triana.techstore.cupon.CuponService;

import lombok.RequiredArgsConstructor;

// Sección de administración de cupones. Permite al admin gestionar los
// cupones públicos y los parámetros del programa de fidelidad.
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/cupones")
public class CuponAdminController {

    private final CuponService cuponService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("publicos", cuponService.findPublicos());
        model.addAttribute("config", cuponService.getConfiguracion());
        return "admin/cupones";
    }

    // Crear cupón público.
    @PostMapping("/nuevo")
    public String crear(@RequestParam String codigo,
                        @RequestParam double descuento,
                        RedirectAttributes ra) {
        try {
            // El admin lo introduce como 25 (no 0.25). Convertimos a fracción.
            cuponService.crearCuponPublico(codigo, descuento / 100.0);
            ra.addFlashAttribute("exitoMensaje", "Cupón \"" + codigo + "\" creado.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorCarrito", e.getMessage());
        }
        return "redirect:/admin/cupones";
    }

    // Activar / desactivar un cupón existente.
    @GetMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id, RedirectAttributes ra) {
        try {
            cuponService.toggleActivo(id);
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorCarrito", e.getMessage());
        }
        return "redirect:/admin/cupones";
    }

    // Eliminar un cupón público (los de fidelidad están protegidos).
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            cuponService.eliminar(id);
            ra.addFlashAttribute("exitoMensaje", "Cupón eliminado.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorCarrito", e.getMessage());
        }
        return "redirect:/admin/cupones";
    }

    // Actualizar la configuración del programa de fidelidad.
    @PostMapping("/config")
    public String configurar(@RequestParam double umbral,
                             @RequestParam double descuento,
                             RedirectAttributes ra) {
        try {
            // descuento llega como 25 (no 0.25).
            cuponService.actualizarConfiguracion(umbral, descuento / 100.0);
            ra.addFlashAttribute("exitoMensaje", "Configuración de fidelidad guardada.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorCarrito", e.getMessage());
        }
        return "redirect:/admin/cupones";
    }
}
