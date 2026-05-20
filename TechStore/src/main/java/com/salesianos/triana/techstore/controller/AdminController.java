package com.salesianos.triana.techstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.salesianos.triana.techstore.model.Producto;
import com.salesianos.triana.techstore.service.ClienteService;
import com.salesianos.triana.techstore.service.PedidoService;
import com.salesianos.triana.techstore.service.ProductoService;

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
    public String nuevoProductoGuardar(@ModelAttribute Producto producto) {
        productoService.save(producto);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/producto/editar/{id}")
    public String editarProductoForm(@PathVariable Long id, Model model) {
        Producto producto = productoService.findById(id).orElseThrow();
        model.addAttribute("producto", producto);
        return "admin/producto/form";
    }

    @PostMapping("/producto/editar/{id}")
    public String editarProductoGuardar(@PathVariable Long id, @ModelAttribute Producto producto) {
        producto.setId(id);
        productoService.edit(producto);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/clientes")
    public String listadoClientes(Model model) {
        model.addAttribute("clientes", clienteService.findAll());
        return "admin/clientes/list";
    }
}
