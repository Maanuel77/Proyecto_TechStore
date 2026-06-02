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
		// "Nuestros favoritos": los 6 productos más vendidos. Pedimos un margen
		// (12) y filtramos archivados para que la sección no muestre productos
		// retirados del catálogo. Si hubiera tantos archivados que no llegamos
		// a 6, simplemente se muestran los que haya — no es crítico.
		var destacados = productoService.findTopVendidos(12).stream()
				.map(ProductoTopDto::producto)
				.filter(p -> !Boolean.TRUE.equals(p.getArchivado()))
				.limit(6)
				.toList();
		model.addAttribute("destacados", destacados);
		return "index";
	}

	@GetMapping("/catalogo")
	public String catalogo(Model model) {
		// Catálogo público: solo productos activos (los archivados están retirados
		// de la venta aunque sigan en BD para preservar los pedidos antiguos).
		model.addAttribute("productos", productoService.findActivos());
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
