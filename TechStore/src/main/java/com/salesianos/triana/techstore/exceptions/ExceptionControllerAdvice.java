package com.salesianos.triana.techstore.exceptions;

import java.util.NoSuchElementException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

/*
 * Controlador global de excepciones (@ControllerAdvice).
 *
 * En lugar de redirigir al usuario a una página dedicada de error (que rompe
 * el flujo de navegación: el usuario tiene que pulsar "volver" y perder el
 * contexto), inyectamos el mensaje como flash attribute y le devolvemos a la
 * página desde la que vino (cabecera Referer). Las plantillas relevantes
 * (catalogo, carrito, dashboard, …) incluyen el fragmento `fragments/alerts`
 * que pinta esos mensajes como banners Bootstrap.
 *
 * Solo se cae a `errores/general` cuando no hay un referer válido al que
 * volver (p.ej. el usuario ha llegado pegando una URL directamente).
 */
@ControllerAdvice
public class ExceptionControllerAdvice {

    // 1. Excepción personalizada de negocio
    @ExceptionHandler(SinStockException.class)
    public String handleSinStock(SinStockException ex,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorCarrito", ex.getMessage());
        return redirectToReferer(request, "/catalogo");
    }

    // 2. Buscar un ID que no existe
    @ExceptionHandler(NoSuchElementException.class)
    public String handleNotFound(NoSuchElementException ex,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorCarrito",
                "El elemento solicitado no existe o ya no está disponible.");
        return redirectToReferer(request, "/catalogo");
    }

    // 3. Argumento ilegal o inválido en lógica de negocio
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex,
                                        HttpServletRequest request,
                                        RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorCarrito", ex.getMessage());
        return redirectToReferer(request, "/");
    }

    /**
     * Devuelve un `redirect:` a la página anterior si el Referer apunta a
     * nuestro propio dominio, o al fallback en caso contrario. Esto evita que
     * un referer externo o nulo nos rompa la redirección.
     */
    private String redirectToReferer(HttpServletRequest request, String fallback) {
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()
                && referer.contains("://" + request.getServerName())) {
            return "redirect:" + referer;
        }
        return "redirect:" + fallback;
    }
}
