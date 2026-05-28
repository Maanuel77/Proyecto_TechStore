package com.salesianos.triana.techstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.salesianos.triana.techstore.dto.ProductoTopDto;
import com.salesianos.triana.techstore.service.ProductoService;
import com.salesianos.triana.techstore.service.VerificacionService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

// Páginas públicas: home, catálogo y login.
@Controller
@RequiredArgsConstructor
public class HomeController {

	private final ProductoService productoService;
	private final VerificacionService verificacionService;

	@GetMapping("/")
	public String index (Model model) {
		// "Nuestros favoritos": los 6 productos más vendidos.
		// findTopVendidos devuelve DTOs; nos quedamos solo con el Producto.
		var destacados = productoService.findTopVendidos(6).stream()
				.map(ProductoTopDto::producto)
				.toList();
		model.addAttribute("destacados", destacados);
		return "index";
	}

	@GetMapping("/catalogo")
	public String catalogo(Model model) {
		model.addAttribute("productos", productoService.findAll());
		return "catalogo";
	}

	@GetMapping("/auth/login")
	public String login(HttpServletRequest request) {
		// Si el usuario vuelve a la pantalla de login estando en mitad de un
		// 2FA, descartamos el código pendiente para no dejar estado huérfano.
		verificacionService.limpiarLogin(request.getSession());
		return "auth/login";
	}
}
