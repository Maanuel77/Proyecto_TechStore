package com.salesianos.triana.techstore.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.salesianos.triana.techstore.service.ProductoService;

import lombok.RequiredArgsConstructor;

// Estadísticas del admin. La primera pestaña: productos más vendidos.
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/estadisticas")
public class EstadisticasController {

    private static final int TOP_LIMIT = 10;

    private final ProductoService productoService;

    @GetMapping
    public String productosMasVendidos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {

        // Si llegan ambas fechas, se filtra; si no, top global.
        List<Object[]> top = (desde != null && hasta != null)
                ? productoService.findTopVendidosBetween(desde, hasta, TOP_LIMIT)
                : productoService.findTopVendidos(TOP_LIMIT);

        model.addAttribute("top", top);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        return "admin/estadisticas/productos";
    }
}
