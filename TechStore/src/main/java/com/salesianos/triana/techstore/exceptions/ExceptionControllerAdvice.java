package com.salesianos.triana.techstore.exceptions;

import java.util.NoSuchElementException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

// Captura las excepciones de la aplicación y, en vez de mostrar una página
// de error que rompa el flujo, devuelve al usuario a la página de la que vino
// (Referer) con un flash message. Las plantillas relevantes incluyen el
// fragmento `fragments/alerts` para mostrarlo como banner Bootstrap.
@ControllerAdvice
public class ExceptionControllerAdvice {

    // Excepción de negocio propia (stock insuficiente).
    @ExceptionHandler(SinStockException.class)
    public String handleSinStock(SinStockException ex,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorCarrito", ex.getMessage());
        return redirectToReferer(request, "/catalogo");
    }

    // ID no encontrado (p.ej. producto borrado mientras el usuario navegaba).
    @ExceptionHandler(NoSuchElementException.class)
    public String handleNotFound(NoSuchElementException ex,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorCarrito",
                "El elemento solicitado no existe o ya no está disponible.");
        return redirectToReferer(request, "/catalogo");
    }

    // Argumentos no válidos en reglas de negocio (precio máximo, FK rota...).
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex,
                                        HttpServletRequest request,
                                        RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorCarrito", ex.getMessage());
        return redirectToReferer(request, "/");
    }

    // Solo aceptamos referers del mismo host para evitar redirects externos.
    private String redirectToReferer(HttpServletRequest request, String fallback) {
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()
                && referer.contains("://" + request.getServerName())) {
            return "redirect:" + referer;
        }
        return "redirect:" + fallback;
    }
}
