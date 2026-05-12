package com.salesianos.triana.techstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.salesianos.triana.techstore.service.PedidoService;
import com.salesianos.triana.techstore.service.ProductoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ProductoService productoService;
    private final PedidoService pedidoService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("productos", productoService.findAll().size());
        model.addAttribute("pedidos", pedidoService.findAll().size());
        model.addAttribute("bajoStock", productoService.lowStock());
        return "admin/dashboard";
    }
}
