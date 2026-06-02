package com.salesianos.triana.techstore.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.webmvc.autoconfigure.error.ErrorViewResolver;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

// Mapea los errores HTTP a nuestras plantillas en templates/errores/ (en español).
//
// Spring Boot por convención busca templates/error/<status>.html (en inglés),
// pero el proyecto tiene la carpeta en castellano. Este resolver toma precedencia
// sobre el DefaultErrorViewResolver y manda cada status code a la plantilla correcta.
//
// Plantillas:
//   - errores/404.html  -> URL no encontrada
//   - errores/general.html -> resto (500, 503, errores inesperados...)
//
// Si en el futuro se añaden plantillas específicas (errores/500.html, etc.),
// basta con extender el switch de abajo.
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomErrorViewResolver implements ErrorViewResolver {

    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request,
                                         HttpStatus status,
                                         Map<String, Object> model) {

        // 404 -> plantilla dedicada (no necesita variables: el texto es fijo).
        if (status == HttpStatus.NOT_FOUND) {
            return new ModelAndView("errores/404", model);
        }

        // Resto -> plantilla genérica. Inyectamos errorTitulo y errorMensaje
        // para que la plantilla los muestre (los espera con th:text).
        // NO se expone el mensaje técnico de la excepción al usuario, solo
        // textos amables; los detalles quedan en los logs del servidor.
        Map<String, Object> data = new HashMap<>(model);
        data.put("errorTitulo", "Algo ha ido mal");
        data.put("errorMensaje", switch (status.series()) {
            case CLIENT_ERROR  -> "No hemos podido procesar tu petición. Revísala e inténtalo de nuevo.";
            case SERVER_ERROR  -> "Estamos teniendo problemas técnicos. Por favor, vuelve a intentarlo en unos minutos.";
            default            -> "Se ha producido un error inesperado.";
        });
        return new ModelAndView("errores/general", data);
    }
}
