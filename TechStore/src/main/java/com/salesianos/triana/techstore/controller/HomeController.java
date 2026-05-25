package com.salesianos.triana.techstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.salesianos.triana.techstore.service.ProductoService;
import lombok.RequiredArgsConstructor;

// Páginas públicas: home, catálogo y login.
@Controller
@RequiredArgsConstructor
public class HomeController {

	private final ProductoService productoService;

	@GetMapping("/")
	public String index (Model model) {
		// En la home solo enseñamos los 6 primeros productos como "destacados".
		model.addAttribute("destacados", productoService.findAll().stream().limit(6).toList());
		return "index";
	}

	@GetMapping("/catalogo")
	public String catalogo(Model model) {
		model.addAttribute("productos", productoService.findAll());
		return "catalogo";
	}

	@GetMapping("/auth/login")
	public String login() {
		return "auth/login";
	}
}
