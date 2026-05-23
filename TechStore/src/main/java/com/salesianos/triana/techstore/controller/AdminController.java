package com.salesianos.triana.techstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.salesianos.triana.techstore.model.Producto;
import com.salesianos.triana.techstore.service.ClienteService;
import com.salesianos.triana.techstore.service.PedidoService;
import com.salesianos.triana.techstore.service.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final ClienteService clienteService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("productos", productoService.findAll().size());
        model.addAttribute("pedidos", pedidoService.findAll().size());
        model.addAttribute("clientes", clienteService.findAll().size());
        model.addAttribute("bajoStock", productoService.lowStock());
        return "admin/dashboard";
    }

    @GetMapping("/producto/nuevo")
    public String nuevoProductoForm(Model model) {
        model.addAttribute("producto", new Producto());
        return "admin/producto/form";
    }

    @PostMapping("/producto/nuevo")
    public String nuevoProductoGuardar(@Valid @ModelAttribute("producto") Producto producto,
                                       BindingResult bindingResult) {
        // Gestión de errores de Validación con @Valid
        if (bindingResult.hasErrors()) {
            // Si hay errores, regresa al formulario manteniendo los mensajes
            return "admin/producto/form";
        }
        // Si el precio supera el límite el service lanza IllegalArgumentException
        // que captura nuestro ExceptionControllerAdvice
        productoService.save(producto);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/clientes")
    public String listadoClientes(Model model) {
        model.addAttribute("clientes", clienteService.findAll());
        return "admin/clientes/list";
    }
}
