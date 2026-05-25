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

/**
 * Maneja los errores 403 de Spring Security (AccessDeniedException) sin
 * mostrar la página "Whitelabel Error" por defecto.
 *
 * Caso típico que queremos resolver: un usuario con rol ADMIN intenta acceder
 * a /carrito o /pedidos (restringidos a CLIENTE en SecurityConfig). En lugar
 * de un error feo, dejamos un mensaje en un FlashMap y le redirigimos a una
 * página válida (el catálogo) donde el banner se muestra y desaparece solo.
 *
 * Como esta excepción la lanza el filtro de Spring Security (no el controlador),
 * el @ControllerAdvice de la app no la captura: hay que registrar este handler
 * directamente en la configuración de seguridad.
 */
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

        // Guardamos el flash en sesión para que sobreviva al redirect.
        new SessionFlashMapManager().saveOutputFlashMap(flashMap, request, response);
        response.sendRedirect(request.getContextPath() + target);
    }
}
