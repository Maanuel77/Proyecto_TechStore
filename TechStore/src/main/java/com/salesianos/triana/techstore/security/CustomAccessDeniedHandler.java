package com.salesianos.triana.techstore.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Maneja los 403 de Spring Security sin mostrar el Whitelabel.
// Como la excepción la lanza el filtro (no el controlador), el
// @ControllerAdvice no la captura: hay que registrar este handler
// directamente en SecurityConfig.
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        FlashMap flashMap = new FlashMap();
        String uri = request.getRequestURI();
        String target;

        // Mensaje y destino según la zona a la que intentaba entrar.
        if (uri.startsWith("/carrito") || uri.startsWith("/pedidos")) {
            flashMap.put("errorAcceso",
                "Los administradores no pueden realizar pedidos ni acceder al historial de cliente. "
              + "Esta funcionalidad es exclusiva de los usuarios con rol CLIENTE.");
            target = "/catalogo";
        } else if (uri.startsWith("/admin")) {
            flashMap.put("errorAcceso",
                "No tienes permisos de administrador para acceder a esa sección.");
            target = "/";
        } else {
            flashMap.put("errorAcceso",
                "No tienes permiso para acceder a esa página.");
            target = "/";
        }

        // El flash se guarda en sesión para sobrevivir al redirect.
        new SessionFlashMapManager().saveOutputFlashMap(flashMap, request, response);
        response.sendRedirect(request.getContextPath() + target);
    }
}
