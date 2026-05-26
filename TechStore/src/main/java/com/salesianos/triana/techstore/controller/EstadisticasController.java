package com.salesianos.triana.techstore.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.salesianos.triana.techstore.dto.ClienteRankingDto;
import com.salesianos.triana.techstore.dto.ProductoTopDto;
import com.salesianos.triana.techstore.service.PedidoService;
import com.salesianos.triana.techstore.service.ProductoService;

import lombok.RequiredArgsConstructor;

// Estadísticas del admin. Cada apartado vive en su propia URL bajo
// /admin/estadisticas y se navega con el subnav de píldoras.
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/estadisticas")
public class EstadisticasController {

    private static final int TOP_LIMIT = 10;
    private static final int CLIENTES_LIMIT = 5;
    // Si el rango es mayor que esto, se agrupa por mes en vez de por día.
    private static final long DIAS_DIARIO_MAX = 60;

    private final ProductoService productoService;
    private final PedidoService pedidoService;

    // La raíz redirige a la primera sección, así no se queda en limbo.
    @GetMapping
    public String index() {
        return "redirect:/admin/estadisticas/productos";
    }

    @GetMapping("/productos")
    public String productosMasVendidos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {

        List<ProductoTopDto> top = (desde != null && hasta != null)
                ? productoService.findTopVendidosBetween(desde, hasta, TOP_LIMIT)
                : productoService.findTopVendidos(TOP_LIMIT);

        model.addAttribute("top", top);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("seccion", "productos");
        return "admin/estadisticas/productos";
    }

    @GetMapping("/pedidos")
    public String pedidosPorFecha(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {

        // Por defecto: últimos 30 días (hasta hoy). Da una vista útil sin filtrar.
        LocalDate rangoHasta = (hasta != null) ? hasta : LocalDate.now();
        LocalDate rangoDesde = (desde != null) ? desde : rangoHasta.minusDays(30);

        // Granularidad automática: diaria si el rango es corto, mensual si es largo.
        long dias = ChronoUnit.DAYS.between(rangoDesde, rangoHasta);
        boolean agruparPorMes = dias > DIAS_DIARIO_MAX;

        // Las dos consultas devuelven records distintos; los pasamos como Object
        // y en la vista accedemos a sus métodos (.fecha, .pedidos, .etiqueta()...).
        List<?> serie = agruparPorMes
                ? pedidoService.findPedidosPorMes(rangoDesde, rangoHasta)
                : pedidoService.findPedidosPorDia(rangoDesde, rangoHasta);

        model.addAttribute("serie", serie);
        model.addAttribute("agruparPorMes", agruparPorMes);
        model.addAttribute("totalPedidos", pedidoService.countPedidosBetween(rangoDesde, rangoHasta));
        model.addAttribute("ingresos", pedidoService.getTotalIngresosBetween(rangoDesde, rangoHasta));
        model.addAttribute("ticketMedio", pedidoService.getTicketMedioBetween(rangoDesde, rangoHasta));
        model.addAttribute("desde", rangoDesde);
        model.addAttribute("hasta", rangoHasta);
        model.addAttribute("seccion", "pedidos");
        return "admin/estadisticas/pedidos";
    }

    // Ranking de clientes: Top 5 y Bottom 5 por gasto total. Si se aplica el
    // filtro de fechas, las dos listas y los KPIs se calculan sobre ese rango.
    @GetMapping("/clientes")
    public String clientesPorGasto(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {

        boolean conRango = (desde != null && hasta != null);

        List<ClienteRankingDto> top = conRango
                ? pedidoService.findTopClientesByGastoBetween(desde, hasta, CLIENTES_LIMIT)
                : pedidoService.findTopClientesByGasto(CLIENTES_LIMIT);

        List<ClienteRankingDto> bottom = conRango
                ? pedidoService.findBottomClientesByGastoBetween(desde, hasta, CLIENTES_LIMIT)
                : pedidoService.findBottomClientesByGasto(CLIENTES_LIMIT);

        // KPIs: nº clientes activos, gasto medio por cliente y top gastador.
        Long activos = conRango
                ? pedidoService.countClientesActivosBetween(desde, hasta)
                : pedidoService.countClientesActivos();
        Double gastoMedio = conRango
                ? pedidoService.getGastoMedioPorClienteBetween(desde, hasta)
                : pedidoService.getGastoMedioPorCliente();
        ClienteRankingDto lider = top.isEmpty() ? null : top.get(0);

        model.addAttribute("top", top);
        model.addAttribute("bottom", bottom);
        model.addAttribute("activos", activos);
        model.addAttribute("gastoMedio", gastoMedio);
        model.addAttribute("lider", lider);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("seccion", "clientes");
        return "admin/estadisticas/clientes";
    }
}
