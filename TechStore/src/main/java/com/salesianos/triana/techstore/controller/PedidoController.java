package com.salesianos.triana.techstore.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.salesianos.triana.techstore.security.User;
import com.salesianos.triana.techstore.service.PedidoService;

import lombok.RequiredArgsConstructor;

// Historial privado de pedidos del cliente. Solo CLIENTE (ver SecurityConfig).
@Controller
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    public String historial(@AuthenticationPrincipal User usuario, Model model) {
        model.addAttribute("pedidos", pedidoService.findByUser(usuario));
        return "pedidos/historial";
    }
}
