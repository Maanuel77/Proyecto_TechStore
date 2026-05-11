/*package com.salesianos.triana.techstore.estadisticas;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.salesianos.triana.techstore.model.Pedido;
import com.salesianos.triana.techstore.model.Producto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EstadisticasController {

	@GetMapping("/estadisticas")
    public String estadisticas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {

        List<Pedido> pedidosFiltrados = (desde != null && hasta != null)
                ? pedidoService.findByFechaBetween(desde, hasta)
                : pedidoService.findAll();

        model.addAttribute("totalIngresos",   pedidoService.getTotalIngresos());
        model.addAttribute("totalPedidos",    pedidoService.findAll().size());
        model.addAttribute("ticketMedio",     pedidoService.getTicketMedio());
        model.addAttribute("clientesActivos", pedidoService.countClientesActivos());

        List<Object[]> masVendidosList = productoService.findMasVendidos();
        model.addAttribute("masVendidos", masVendidosList);
        model.addAttribute("chartProductNames",
                masVendidosList.stream().limit(6).map(r -> ((Producto) r[0]).getNombre()).toList());
        model.addAttribute("chartProductQtys",
                masVendidosList.stream().limit(6).map(r -> r[1]).toList());

        List<Object[]> ventasMarcaList = productoService.findVentasPorMarca();
        model.addAttribute("chartMarcaNames", ventasMarcaList.stream().map(r -> (String) r[0]).toList());
        model.addAttribute("chartMarcaQtys",  ventasMarcaList.stream().map(r -> r[1]).toList());

        model.addAttribute("mayorGasto",       pedidoService.findClientesConMayorGasto());
        model.addAttribute("bajoStock",        productoService.lowStock());
        model.addAttribute("pedidosFiltrados", pedidosFiltrados);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta",  hasta);
        return "admin/estadisticas";
    }
	
}*/
